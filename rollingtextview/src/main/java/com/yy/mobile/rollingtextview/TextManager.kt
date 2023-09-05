package com.yy.mobile.rollingtextview

import android.graphics.Canvas
import android.graphics.Paint
import java.util.Collections
import kotlin.math.max

/**
 * @author YvesCheung
 * 2018/2/26
 */
internal class TextManager(
    private val textPaint: Paint,
    private val charOrderManager: CharOrderManager
) {

    companion object {
        const val EMPTY: Char = 0.toChar()
    }

    private val map: MutableMap<Char, Float> = LinkedHashMap(36)

    private val textColumns = mutableListOf<TextColumn>()

    private var charListColumns: List<List<Char>> = Collections.emptyList()

    var letterSpacingExtra: Int = 0

    init {
        updateFontMetrics()
    }

    fun charWidth(c: Char, textPaint: Paint): Float {
        return if (c == EMPTY) {
            0f
        } else {
            map[c] ?: textPaint.measureText(c.toString()).also { map[c] = it }
        }
    }

    fun updateFontMetrics() {
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
            val nextProgress = charOrderManager.getProgress(
                previousProgress, index,
                charListColumns, column.index
            )

            val previous = column.onAnimationUpdate(
                nextProgress.currentIndex,
                nextProgress.offsetPercentage, nextProgress.progress
            )
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
            canvas.translate(it.currentWidth + letterSpacingExtra, 0f)
        }
    }

    val currentTextWidth: Float
        get() {
            val space = letterSpacingExtra * max(0, textColumns.size - 1)
            val textWidth = textColumns
                .map { it.currentWidth }
                .fold(0f) { total, next -> total + next }
            return textWidth + space
        }

    fun setText(targetText: CharSequence) {
        val sourceText = String(currentText)

        val maxLen = max(sourceText.length, targetText.length)

        charOrderManager.beforeCharOrder(sourceText, targetText)
        textColumns.clear()
        for (idx in 0 until maxLen) {
            val (list, direction) = charOrderManager.findCharOrder(sourceText, targetText, idx)
            textColumns.add(TextColumn(this, idx, textPaint, list, direction))
        }
        charListColumns = textColumns.map { it.changeCharList }
    }

    val currentText
        get(): CharArray = CharArray(textColumns.size) { index -> textColumns[index].currentChar }

    var textHeight: Float = 0f
        private set

    var textBaseline = 0f
        private set
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