package com.yy.mobile.rollingtextview

import android.graphics.Canvas
import android.graphics.Paint
import com.yy.mobile.rollingtextview.TextManager.Companion.EMPTY

/**
 * Created by 张宇 on 2018/2/26.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
@Suppress("MemberVisibilityCanBePrivate")
internal class TextColumn(
        private val manager: TextManager,
        private val textPaint: Paint,
        var changeCharList: List<Char>,
        var direction: Direction) {

    var currentWidth: Float = 0f

    var currentChar: Char = EMPTY
        private set(value) {
            field = value
        }

    val sourceChar
        get() = if (changeCharList.size < 2) EMPTY else changeCharList.first()

    val targetChar
        get() = if (changeCharList.isEmpty()) EMPTY else changeCharList.last()

    private var sourceWidth = 0f

    private var targetWidth = 0f

    private var previousBottomDelta = 0.0
    private var bottomDelta = 0.0

    private var index = 0

    init {
        initChangeCharList()
    }

    fun measure() {
        sourceWidth = manager.charWidth(sourceChar, textPaint)
        targetWidth = manager.charWidth(targetChar, textPaint)
        currentWidth = Math.max(sourceWidth, targetWidth)
    }

    fun setChangeCharList(charList: List<Char>, dir: Direction) {
        changeCharList = charList
        direction = dir
        initChangeCharList()
        index = 0
        previousBottomDelta = bottomDelta
        bottomDelta = 0.0
    }

    private fun initChangeCharList() {
        //没有动画的情况
        if (changeCharList.size < 2) {
            currentChar = targetChar
        }
        //重新计算字符宽度
        measure()
    }

    fun updateAnimation(progress: Float) {

        //当changeCharList.size大于7位数的时候 有可能使Float溢出 所以要用Double
        val progressDouble: Double = progress.toDouble()

        //相对于字符序列的进度
        val sizeProgress = (changeCharList.size - 1) * progressDouble

        //通过进度获得当前字符
        index = sizeProgress.toInt()
        currentChar = changeCharList[index]

        //求底部偏移值
        val offsetPercentage = sizeProgress - index
        //从上一次动画结束时的偏移值开始
        val additionalDelta = previousBottomDelta * (1f - progress)
        bottomDelta = offsetPercentage * manager.textHeight * direction.value + additionalDelta

        //计算当前字符宽度，为当前字符和下一个字符的过渡宽度
        val charWidth = manager.charWidth(currentChar, textPaint)
        currentWidth = if (index + 1 < changeCharList.size) {
            val nextCharWidth = manager.charWidth(changeCharList[index + 1], textPaint)
//            charWidth + (nextCharWidth - charWidth) * progress
            Math.max(charWidth, nextCharWidth)
        } else {
            charWidth
        }
    }

    fun onAnimationEnd() {
        currentChar = targetChar
        bottomDelta = 0.0
        previousBottomDelta = 0.0
    }

    fun draw(canvas: Canvas) {

        fun drawText(idx: Int, verticalOffset: Float) {

            fun charAt(idx: Int) = CharArray(1) { changeCharList[idx] }

            if (idx >= 0 && idx < changeCharList.size && changeCharList[idx] != EMPTY) {
                canvas.drawText(charAt(idx), 0, 1, 0f, verticalOffset, textPaint)
            }
        }

        drawText(index + 1, bottomDelta.toFloat() - manager.textHeight * direction.value)
        drawText(index, bottomDelta.toFloat())
        drawText(index - 1, bottomDelta.toFloat() + manager.textHeight * direction.value)
    }
}