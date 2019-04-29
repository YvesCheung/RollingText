package com.yy.mobile.rollingtextview

import android.graphics.Canvas
import android.graphics.Paint
import java.util.*

/**
 * Created by 张宇 on 2018/2/26.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
internal class TextManager(
    private val textPaint: Paint,
    private val charOrderManager: CharOrderManager
) {

    companion object {
        const val EMPTY: Char = 0.toChar()

        const val FLT_EPSILON: Float = 1.192092896e-07F
    }

    private val map: MutableMap<Char, Float> = LinkedHashMap(36)

    private val textColumns = mutableListOf<TextColumn>()

    private var charListColumns: List<List<Char>> = Collections.emptyList()

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
        //当changeCharList.size大于7位数的时候 有可能使Float溢出 所以要用Double
        val initialize = PreviousProgress(0, 0.0, progress.toDouble())
        textColumns.foldRightIndexed(initialize) { index, column, previousProgress ->
            val nextProgress = charOrderManager.getProgress(previousProgress, index,
                charListColumns, column.index)

            val previous = column.onAnimationUpdate(nextProgress.currentIndex,
                nextProgress.offsetPercentage, nextProgress.progress)
            previous
        }
    }

    fun onAnimationEnd() {
        textColumns.forEach { it.onAnimationEnd() }
        charOrderManager.afterCharOrder()
    }

    fun draw(canvas: Canvas) {
        textColumns.forEach {
            it.draw(canvas)
            canvas.translate(it.currentWidth, 0f)
        }
    }

    val currentTextWidth: Float
        get() = textColumns.map { it.currentWidth }.fold(0f) { total, next -> total + next }

    private fun Float.isZero(): Boolean = this < FLT_EPSILON && this > -FLT_EPSILON

    fun setText(targetText: CharSequence) {

//        val itr = textColumns.iterator()
//        while (itr.hasNext()) {
//            val column = itr.next()
//            if (column.currentWidth.isZero()) {
//                itr.remove()
//            }
//        }

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
        charListColumns = textColumns.map { it.changeCharList }
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

data class PreviousProgress(
    val currentIndex: Int,
    val offsetPercentage: Double,
    val progress: Double,
    val currentChar: Char = TextManager.EMPTY,
    val currentWidth: Float = 0f
)

data class NextProgress(
    val currentIndex: Int,
    val offsetPercentage: Double,
    val progress: Double
)