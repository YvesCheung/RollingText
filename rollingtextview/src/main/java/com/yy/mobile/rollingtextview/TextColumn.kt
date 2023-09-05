package com.yy.mobile.rollingtextview

import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout.HORIZONTAL
import com.yy.mobile.rollingtextview.TextManager.Companion.EMPTY
import com.yy.mobile.rollingtextview.strategy.Direction
import kotlin.math.min

/**
 * @author YvesCheung
 * 2018/2/26
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
internal class TextColumn(
    private val manager: TextManager,
    private val column: Int,
    private val textPaint: Paint,
    var changeCharList: List<Char>,
    var direction: Direction
) {
    var currentWidth: Float = 0f

    private var animateStartWidth = 0f

    var currentChar: Char =
        if (changeCharList.size < 2) { //没有动画的情况
            targetChar
        } else {
            sourceChar
        }
        private set

    val sourceChar
        get() = if (changeCharList.size < 2) EMPTY else changeCharList.first()

    val targetChar
        get() = if (changeCharList.isEmpty()) EMPTY else changeCharList.last()

    private var previousEdgeDelta = 0.0
    private var edgeDelta = 0.0

    var index = 0
        private set

    init {
        measure()
    }

    fun measure() {
        currentWidth = manager.charWidth(currentChar, textPaint)
        animateStartWidth = currentWidth
    }

    fun onAnimationUpdate(
        currentIndex: Int,
        offsetPercentage: Double,
        progress: Double
    ): PreviousProgress {
        if (index != currentIndex) {
            animateStartWidth = currentWidth
        }
        //当前字符
        index = currentIndex
        currentChar = changeCharList[currentIndex]

        //从上一次动画结束时的偏移值开始
        val additionalDelta = previousEdgeDelta * (1.0 - progress)
        edgeDelta =
            if (direction.orientation == HORIZONTAL) {
                offsetPercentage * currentWidth * direction.value + additionalDelta
            } else {
                offsetPercentage * manager.textHeight * direction.value + additionalDelta
            }

        //计算当前字符宽度，为上一个字符到下一个字符的过渡宽度
        val targetWidth =
            if (offsetPercentage <= 0.5f) {
                manager.charWidth(currentChar, textPaint)
            } else {
                val nextChar = changeCharList[min(currentIndex + 1, changeCharList.lastIndex)]
                manager.charWidth(nextChar, textPaint)
            }
        currentWidth =
            if (offsetPercentage <= 0.0) {
                targetWidth
            } else {
                ((targetWidth - animateStartWidth) * offsetPercentage + animateStartWidth).toFloat()
            }
        return PreviousProgress(index, offsetPercentage, progress, currentChar, currentWidth)
    }

    fun onAnimationEnd() {
        currentChar = targetChar
        edgeDelta = 0.0
        previousEdgeDelta = 0.0
    }

    fun draw(canvas: Canvas) {
        fun drawText(idx: Int, horizontalOffset: Float = 0f, verticalOffset: Float = 0f) {

            fun charAt(idx: Int) = CharArray(1) { changeCharList[idx] }

            if (idx >= 0 && idx < changeCharList.size && changeCharList[idx] != EMPTY) {
                canvas.drawText(charAt(idx), 0, 1, horizontalOffset, verticalOffset, textPaint)
            }
        }

        if (direction.orientation == HORIZONTAL) {
            drawText(index + 1, horizontalOffset = edgeDelta.toFloat() - currentWidth * direction.value)
            drawText(index, horizontalOffset = edgeDelta.toFloat())
            drawText(index - 1, horizontalOffset = edgeDelta.toFloat() + currentWidth * direction.value)
        } else {
            drawText(index + 1, verticalOffset = edgeDelta.toFloat() - manager.textHeight * direction.value)
            drawText(index, verticalOffset = edgeDelta.toFloat())
            drawText(index - 1, verticalOffset = edgeDelta.toFloat() + manager.textHeight * direction.value)
        }
    }
}