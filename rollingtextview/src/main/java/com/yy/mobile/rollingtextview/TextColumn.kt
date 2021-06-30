package com.yy.mobile.rollingtextview

import android.graphics.Canvas
import android.graphics.Paint
import android.widget.LinearLayout.HORIZONTAL
import com.yy.mobile.rollingtextview.TextManager.Companion.EMPTY
import com.yy.mobile.rollingtextview.strategy.Direction

/**
 * @author YvesCheung
 * 2018/2/26
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class TextColumn(
    private val manager: TextManager,
    private val textPaint: Paint,
    var changeCharList: List<Char>,
    var direction: Direction
) {

    var currentWidth: Float = 0f

    var currentChar: Char = EMPTY
        private set

    val sourceChar
        get() = if (changeCharList.size < 2) EMPTY else changeCharList.first()

    val targetChar
        get() = if (changeCharList.isEmpty()) EMPTY else changeCharList.last()

    private var sourceWidth = 0f

    private var targetWidth = 0f

    private var previousEdgeDelta = 0.0
    private var edgeDelta = 0.0

    var index = 0

    private var firstNotEmptyChar: Char = EMPTY
    private var firstCharWidth: Float = 0f
    private var lastNotEmptyChar: Char = EMPTY
    private var lastCharWidth: Float = 0f

    init {
        initChangeCharList()
    }

    fun measure() {
        sourceWidth = manager.charWidth(sourceChar, textPaint)
        targetWidth = manager.charWidth(targetChar, textPaint)
        currentWidth = Math.max(sourceWidth, firstCharWidth)
    }

    fun setChangeCharList(charList: List<Char>, dir: Direction) {
        changeCharList = charList
        direction = dir
        initChangeCharList()
        index = 0
        previousEdgeDelta = edgeDelta
        edgeDelta = 0.0
    }

    private fun initChangeCharList() {
        //没有动画的情况
        if (changeCharList.size < 2) {
            currentChar = targetChar
        }
        firstNotEmptyChar = changeCharList.firstOrNull { it != EMPTY } ?: EMPTY
        firstCharWidth = manager.charWidth(firstNotEmptyChar, textPaint)
        lastNotEmptyChar = changeCharList.lastOrNull { it != EMPTY } ?: EMPTY
        lastCharWidth = manager.charWidth(lastNotEmptyChar, textPaint)
        //重新计算字符宽度
        measure()
    }

    fun onAnimationUpdate(
        currentIndex: Int,
        offsetPercentage: Double,
        progress: Double
    ): PreviousProgress {

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

        //计算当前字符宽度，为第一个字符和最后一个字符的过渡宽度
        currentWidth = if (currentChar.toInt() > 0) {
            (lastCharWidth - firstCharWidth) * progress.toFloat() + firstCharWidth
        } else {
            0f
        }

        return PreviousProgress(index, offsetPercentage, progress, currentChar, currentWidth)
    }

    fun onAnimationEnd() {
        currentChar = targetChar
        edgeDelta = 0.0
        previousEdgeDelta = 0.0
    }

    fun draw(canvas: Canvas) {
        val cs = canvas.save()
        val originRect = canvas.clipBounds
        canvas.clipRect(0, originRect.top, currentWidth.toInt(), originRect.bottom)


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

        canvas.restoreToCount(cs)
    }
}