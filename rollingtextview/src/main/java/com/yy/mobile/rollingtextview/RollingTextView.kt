package com.yy.mobile.rollingtextview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.TargetApi
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

/**
 * Created by 张宇 on 2018/2/26.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class RollingTextView : View {

    private var lastMeasuredDesiredWidth: Int = 0
    private var lastMeasuredDesiredHeight: Int = 0

    private val textPaint = Paint()
    private val charOrderManager = CharOrderManager()
    private val textManager = TextManager(textPaint, charOrderManager)

    private var animator = ValueAnimator.ofFloat(1f)

    private val viewBounds = Rect()
    private var gravity: Int = Gravity.END

    constructor(context: Context) : super(context) {
        init(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
        init(context, attrs, defStyleAttr, 0)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs, defStyleAttr, defStyleRes)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
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
        val widthChanged = lastMeasuredDesiredWidth != computeDesiredWidth()
        val heightChanged = lastMeasuredDesiredHeight != computeDesiredHeight()

        if (widthChanged || heightChanged) {
            requestLayout()
            return true
        }
        return false
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
            translationY = 0f
        }
        if (gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
            translationY = viewBounds.top + (availableHeight - currentHeight)
        }
        if (gravity and Gravity.START == Gravity.START) {
            translationX = 0f
        }
        if (gravity and Gravity.END == Gravity.END) {
            translationX = viewBounds.left + (availableWidth - currentWidth)
        }

        canvas.translate(translationX, translationY)
        canvas.clipRect(0f, 0f, currentWidth, currentHeight)
    }


    /***************************** Public API below ***********************************************/

    var animationDuration: Long = 1500L

    private var targetText: CharSequence = ""

    var text: CharSequence
        set(value) = setText(value, !TextUtils.isEmpty(targetText))
        get() = targetText

    fun setText(text: CharSequence, animate: Boolean) {
        targetText = text
        if (animate) {
            textManager.setText(text)
            with(animator) {
                if (isRunning) {
                    cancel()
                }
                duration = animationDuration
//                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
        } else {
            val orginalStrategy = charStrategy
            charStrategy = NoAnimation
            textManager.setText(text)
            charStrategy = orginalStrategy

            textManager.onAnimationEnd()
            checkForReLayout()
            invalidate()
        }
    }

    val currentText
        get() = textManager.currentText

    var textSize: Float
        set(value) = setTextSize(TypedValue.COMPLEX_UNIT_SP, value)
        get() = textPaint.textSize

    fun setTextSize(unit: Int, size: Float) {
        val r: Resources = context?.resources ?: Resources.getSystem()
        textPaint.textSize = TypedValue.applyDimension(unit, size, r.displayMetrics)
        textManager.updateFontMatrics()
    }

    var charStrategy: CharOrderStrategy
        set(value) {
            charOrderManager.charStrategy = value
        }
        get() = charOrderManager.charStrategy

    fun addCharOrder(orderList: CharSequence) = charOrderManager.addCharOrder(orderList.asIterable())

    fun addCharOrder(orderList: Collection<Char>) = charOrderManager.addCharOrder(orderList)

    fun addCharOrder(orderList: Array<Char>) = charOrderManager.addCharOrder(orderList.asIterable())

    fun addAnimatorListener(listener: Animator.AnimatorListener) = animator.addListener(listener)

    fun removeAnimatorListener(listener: Animator.AnimatorListener) = animator.removeListener(listener)
}