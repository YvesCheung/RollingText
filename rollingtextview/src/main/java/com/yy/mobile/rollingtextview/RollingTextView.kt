@file:Suppress("unused")

package com.yy.mobile.rollingtextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import com.yy.mobile.rollingtextview.strategy.CharOrderStrategy
import com.yy.mobile.rollingtextview.strategy.Strategy

/**
 * @author YvesCheung
 * 2018/2/26
 */
@Suppress("MemberVisibilityCanBePrivate")
open class RollingTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var lastMeasuredDesiredWidth: Int = 0
    private var lastMeasuredDesiredHeight: Int = 0

    private val textPaint = Paint()
    private val charOrderManager = CharOrderManager()
    private val textManager = TextManager(textPaint, charOrderManager)

    private var animator = ValueAnimator.ofFloat(1f)

    private val viewBounds = Rect()
    private var gravity: Int = Gravity.END
    private var textStyle = Typeface.NORMAL

    private var targetText: CharSequence = ""

    var animationDuration: Long = 750L

    var typeface: Typeface?
        set(value) {
            textPaint.typeface = when (textStyle) {
                Typeface.BOLD_ITALIC -> Typeface.create(value, Typeface.BOLD_ITALIC)
                Typeface.BOLD -> Typeface.create(value, Typeface.BOLD)
                Typeface.ITALIC -> Typeface.create(value, Typeface.ITALIC)
                else -> value
            }
            onTextPaintMeasurementChanged()
        }
        get() = textPaint.typeface

    init {
        var shadowColor = 0
        var shadowDx = 0f
        var shadowDy = 0f
        var shadowRadius = 0f
        var text = ""
        var textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            12f, context.resources.displayMetrics)

        fun applyTypedArray(arr: TypedArray) {
            gravity = arr.getInt(R.styleable.RollingTextView_android_gravity, gravity)
            shadowColor = arr.getColor(R.styleable.RollingTextView_android_shadowColor, shadowColor)
            shadowDx = arr.getFloat(R.styleable.RollingTextView_android_shadowDx, shadowDx)
            shadowDy = arr.getFloat(R.styleable.RollingTextView_android_shadowDy, shadowDy)
            shadowRadius = arr.getFloat(R.styleable.RollingTextView_android_shadowRadius, shadowRadius)
            text = arr.getString(R.styleable.RollingTextView_android_text) ?: ""
            textColor = arr.getColor(R.styleable.RollingTextView_android_textColor, textColor)
            textSize = arr.getDimension(R.styleable.RollingTextView_android_textSize, textSize)
            textStyle = arr.getInt(R.styleable.RollingTextView_android_textStyle, textStyle)
        }

        val arr = context.obtainStyledAttributes(attrs, R.styleable.RollingTextView,
            defStyleAttr, defStyleRes)

        val textAppearanceResId = arr.getResourceId(
            R.styleable.RollingTextView_android_textAppearance, -1)

        if (textAppearanceResId != -1) {
            val textAppearanceArr = context.obtainStyledAttributes(
                textAppearanceResId, R.styleable.RollingTextView)
            applyTypedArray(textAppearanceArr)
            textAppearanceArr.recycle()
        }

        applyTypedArray(arr)

        animationDuration = arr.getInt(R.styleable.RollingTextView_duration, animationDuration.toInt()).toLong()

        textPaint.isAntiAlias = true
        if (shadowColor != 0) {
            textPaint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor)
        }
        if (textStyle != 0) {
            typeface = textPaint.typeface
        }

        setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize)
        setText(text, false)

        arr.recycle()

        animator.addUpdateListener {
            textManager.updateAnimation(it.animatedFraction)
            checkForReLayout()
            invalidate()
        }
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                textManager.onAnimationEnd()
            }
        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()

        realignAndClipCanvasForGravity(canvas)

        canvas.translate(0f, textManager.textBaseline)

        textManager.draw(canvas)

        canvas.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        lastMeasuredDesiredWidth = computeDesiredWidth()
        lastMeasuredDesiredHeight = computeDesiredHeight()

        val desiredWidth = resolveSize(lastMeasuredDesiredWidth, widthMeasureSpec)
        val desiredHeight = resolveSize(lastMeasuredDesiredHeight, heightMeasureSpec)

        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onSizeChanged(width: Int, height: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(width, height, oldw, oldh)
        viewBounds.set(paddingLeft, paddingTop, width - paddingRight,
            height - paddingBottom)
    }

    private fun checkForReLayout(): Boolean {
        //        val widthChanged = lastMeasuredDesiredWidth != computeDesiredWidth()
        //        val heightChanged = lastMeasuredDesiredHeight != computeDesiredHeight()
        //
        //        if (widthChanged || heightChanged) {
        //            requestLayout()
        //            return true
        //        }
        //        return false
        requestLayout()
        return true
    }

    private fun computeDesiredWidth(): Int {
        return textManager.currentTextWidth.toInt() + paddingLeft + paddingRight
    }

    private fun computeDesiredHeight(): Int {
        return textManager.textHeight.toInt() + paddingTop + paddingBottom
    }

    private fun realignAndClipCanvasForGravity(canvas: Canvas) {
        val currentWidth = textManager.currentTextWidth
        val currentHeight = textManager.textHeight
        val availableWidth = viewBounds.width()
        val availableHeight = viewBounds.height()
        var translationX = 0f
        var translationY = 0f
        if (gravity and Gravity.CENTER_VERTICAL == Gravity.CENTER_VERTICAL) {
            translationY = viewBounds.top + (availableHeight - currentHeight) / 2f
        }
        if (gravity and Gravity.CENTER_HORIZONTAL == Gravity.CENTER_HORIZONTAL) {
            translationX = viewBounds.left + (availableWidth - currentWidth) / 2f
        }
        if (gravity and Gravity.TOP == Gravity.TOP) {
            translationY = viewBounds.top.toFloat()
        }
        if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
            translationY = viewBounds.top + (availableHeight - currentHeight)
        }
        if (gravity and Gravity.START == Gravity.START) {
            translationX = viewBounds.left.toFloat()
        }
        if (gravity and Gravity.END == Gravity.END) {
            translationX = viewBounds.left + (availableWidth - currentWidth)
        }

        canvas.translate(translationX, translationY)
        canvas.clipRect(0f, 0f, currentWidth, currentHeight)
    }

    private fun onTextPaintMeasurementChanged() {
        textManager.updateFontMetrics()
        checkForReLayout()
        invalidate()
    }

    /***************************** Public API below ***********************************************/

    var animationInterpolator: Interpolator = LinearInterpolator()

    /**
     * @param text 设置文本
     */
    fun setText(text: CharSequence) = setText(text, !TextUtils.isEmpty(targetText))

    fun getText() = targetText

    /**
     * @param text 设置文本
     * @param animate 是否需要滚动效果
     */
    fun setText(text: CharSequence, animate: Boolean) {
        targetText = text
        if (animate) {
            textManager.setText(text)
            with(animator) {
                if (isRunning) {
                    cancel()
                }
                duration = animationDuration
                interpolator = animationInterpolator
                //到下一次looper去开始新的动画，解决在onAnimationEnd的时候setText的问题
                post {
                    start()
                }
            }
        } else {
            val originalStrategy = charStrategy
            charStrategy = Strategy.NoAnimation()
            textManager.setText(text)
            charStrategy = originalStrategy

            textManager.onAnimationEnd()
            checkForReLayout()
            invalidate()
        }
    }

    val currentText
        get() = textManager.currentText

    fun setTextSize(textSize: Float) = setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

    fun getTextSize() = textPaint.textSize

    fun setTextSize(unit: Int, size: Float) {
        val r: Resources = context?.resources ?: Resources.getSystem()
        textPaint.textSize = TypedValue.applyDimension(unit, size, r.displayMetrics)
        onTextPaintMeasurementChanged()
    }

    var textColor: Int = Color.BLACK
        set(color) {
            if (field != color) {
                field = color
                textPaint.color = color
                invalidate()
            }
        }

    /**
     * px between letter
     */
    var letterSpacingExtra: Int
        set(value) {
            textManager.letterSpacingExtra = value
        }
        get() = textManager.letterSpacingExtra

    override fun getBaseline(): Int {
        val fontMetrics = textPaint.fontMetrics
        return (textManager.textHeight / 2 + ((fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent)).toInt()
    }

    /**
     * 设置动画滚动策略,如：
     *
     * 直接更替字符串
     * [Strategy.NoAnimation]
     *
     * 普通滚动
     * [Strategy.NormalAnimation]
     *
     * 指定方向滚动
     * [Strategy.SameDirectionAnimation]
     *
     * 进位滚动
     * [Strategy.CarryBitAnimation]
     */
    var charStrategy: CharOrderStrategy
        set(value) {
            charOrderManager.charStrategy = value
        }
        get() = charOrderManager.charStrategy

    /**
     * 添加动画监听器
     */
    fun addAnimatorListener(listener: Animator.AnimatorListener) = animator.addListener(listener)

    /**
     * 移除动画监听器
     */
    fun removeAnimatorListener(listener: Animator.AnimatorListener) = animator.removeListener(listener)

    /**
     * 添加支持的序列，如[CharOrder.Number]/[CharOrder.Alphabet]
     *
     * 如果orderList为[2,4,6,8,0]，那么从"440"到"844"的动画将会是"440"->"642"->"844"
     *
     * 与[charStrategy]配合使用定义动画效果
     */
    fun addCharOrder(orderList: CharSequence) = charOrderManager.addCharOrder(orderList.asIterable())

    /**
     * 添加支持的序列，如[CharOrder.Number]/[CharOrder.Alphabet]
     *
     * 如果orderList为[2,4,6,8,0]，那么从"440"到"844"的动画将会是"440"->"642"->"844"
     *
     * 与[charStrategy]配合使用定义动画效果
     */
    fun addCharOrder(orderList: Iterable<Char>) = charOrderManager.addCharOrder(orderList)

    /**
     * 添加支持的序列，如[CharOrder.Number]/[CharOrder.Alphabet]
     *
     * 如果orderList为[2,4,6,8,0]，那么从"440"到"844"的动画将会是"440"->"642"->"844"
     *
     * 与[charStrategy]配合使用定义动画效果
     */
    fun addCharOrder(orderList: Array<Char>) = charOrderManager.addCharOrder(orderList.asIterable())
}