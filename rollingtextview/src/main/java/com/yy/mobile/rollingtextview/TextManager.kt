package com.yy.mobile.rollingtextview

import android.graphics.Canvas
import android.graphics.Paint

/**
 * Created by 张宇 on 2018/2/26.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
internal class TextManager(
        private val textPaint: Paint,
        private val charOrderManager: CharOrderManager) {

    companion object {
        const val EMPTY: Char = 0.toChar()
    }

    private val map: MutableMap<Char, Float> = LinkedHashMap(36)

    private val textColumns = mutableListOf<TextColumn>()

    init {
        updateFontMatrics()
    }

    fun charWidth(c: Char, textPaint: Paint): Float {
        return if (c == EMPTY) {
            0f
        } else {
            map[c] ?: textPaint.measureText(c.toString()).also { map[c] = it }
        }
    }

    fun updateFontMatrics() {
        map.clear()
        with(textPaint.fontMetrics) {
            textHeight = bottom - top
            textBaseline = -top
        }
        textColumns.forEach { it.measure() }
    }

    fun updateAnimation(progress: Float) {
        textColumns.forEach {
            it.updateAnimation(progress)
        }
    }

    fun onAnimationEnd() {
        textColumns.forEach { it.onAnimationEnd() }
    }

    fun draw(canvas: Canvas) {
        textColumns.forEach {
            it.draw(canvas)
            canvas.translate(it.currentWidth, 0f)
        }
    }

    val currentTextWidth: Float
        get() = textColumns.map { it.currentWidth }.fold(0f) { total, next -> total + next }

    fun setText(targetText: CharSequence) {

        val itr = textColumns.iterator()
        while (itr.hasNext()) {
            val column = itr.next()
            if (column.currentWidth.toInt() == 0) {
                itr.remove()
            }
        }

        val sourceText = String(currentText)

        val maxLen = Math.max(sourceText.length, targetText.length)

        charOrderManager.beforeCharOrder(sourceText, targetText)
        for (idx in 0 until maxLen) {
            val (list, direction) = charOrderManager.findCharOrder(sourceText, targetText, idx)

            if (idx >= maxLen - sourceText.length) {
                textColumns[idx].setChangeCharList(list, direction)
            } else {
                textColumns.add(idx, TextColumn(this, textPaint, list, direction))
            }
        }
        charOrderManager.afterCharOrder(sourceText, targetText)
    }

    val currentText
        get(): CharArray = CharArray(textColumns.size) { index -> textColumns[index].currentChar }

    var textHeight: Float = 0f
        private set(value) {
            field = value
        }

    var textBaseline = 0f
        private set(value) {
            field = value
        }
}