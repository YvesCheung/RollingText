package com.yy.mobile.rollingtextview.strategy

import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.VERTICAL
import com.yy.mobile.rollingtextview.NextProgress
import com.yy.mobile.rollingtextview.PreviousProgress
import com.yy.mobile.rollingtextview.TextManager
import kotlin.math.max

/**
 * Created by 张宇 on 2018/2/28.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 *
 *
 * 字符滚动变化的策略
 * strategy to determine how characters change
 */
interface CharOrderStrategy {

    /**
     * 在滚动动画计算前回调，可以做初始化的事情
     *
     * Notifies the animation calculation will start immediately,
     * you can override this method and do some initialization work
     *
     * @param sourceText 原来的文本
     * @param targetText 动画后的目标文本
     * @param charPool 外部设定的可选的字符变化序列
     */
    fun beforeCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {}

    /**
     * 从[sourceText]滚动变化到[targetText]，对于索引[index]的位置，给出变化的字符顺序
     *
     * 也可以直接继承[SimpleCharOrderStrategy]，可以更简单的实现策略
     *
     * you need to override this method to tell me how the animation should behave.
     * this method will be invoked many times with different index, which is from 0 to
     * max(sourceText.length, targetText.length)
     *
     * @param sourceText 原来的文本
     * @param targetText 动画后的目标文本
     * @param index 当前字符的位置 范围[0,Math.max(sourceText.length,targetText.length)]
     * @param charPool 外部设定的可选的字符变化序列
     */
    fun findCharOrder(sourceText: CharSequence,
                      targetText: CharSequence,
                      index: Int,
                      charPool: CharPool): Pair<List<Char>, Direction>


    fun nextProgress(
            previousProgress: PreviousProgress,
            columnIndex: Int,
            columns: List<List<Char>>,
            charIndex: Int): NextProgress

    /**
     * 在滚动动画计算后回调
     *
     * you can override this method to clean up after animation
     */
    fun afterCompute() {}
}

/**
 * 简单的策略模版：在[findCharOrder]中选择一个重写即可
 *
 * a simple strategy template
 */
abstract class SimpleCharOrderStrategy : CharOrderStrategy {

    override fun beforeCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {}

    override fun afterCompute() {}

    override fun nextProgress(
            previousProgress: PreviousProgress,
            columnIndex: Int,
            columns: List<List<Char>>,
            charIndex: Int): NextProgress {

        val columnSize = columns.size
        val charList = columns[columnIndex]
        val factor = getFactor(previousProgress, columnIndex, columnSize, charList)
        //相对于字符序列的进度
        val sizeProgress = (charList.size - 1) * previousProgress.progress

        //通过进度获得当前字符
        val currentCharIndex = sizeProgress.toInt()

        //求底部偏移值
        val k = 1.0 / factor
        val b = (1.0 - factor) * k
        val offset = sizeProgress - currentCharIndex
        val offsetPercentage = if (offset >= 1.0 - factor) offset * k - b else 0.0

        return NextProgress(currentCharIndex, offsetPercentage, previousProgress.progress)
    }

    open fun getFactor(previousProgress: PreviousProgress,
                       index: Int,
                       size: Int,
                       charList: List<Char>): Double = 1.0

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool): Pair<List<Char>, Direction> {

        val maxLen = max(sourceText.length, targetText.length)
        val disSrc = maxLen - sourceText.length
        val disTgt = maxLen - targetText.length

        var srcChar = TextManager.EMPTY
        var tgtChar = TextManager.EMPTY
        if (index >= disSrc) {
            srcChar = sourceText[index - disSrc]
        }
        if (index >= disTgt) {
            tgtChar = targetText[index - disTgt]
        }

        return findCharOrder(srcChar, tgtChar, index, charPool)
    }

    /**
     * 从字符[sourceChar]滚动变化到[targetChar]的变化顺序
     *
     * @param sourceChar 原字符
     * @param targetChar 滚动变化后的目标字符
     * @param index 字符索引
     * @param charPool 外部设定的序列，如果没设定则为空
     */
    open fun findCharOrder(sourceChar: Char, targetChar: Char, index: Int, charPool: CharPool)
            : Pair<List<Char>, Direction> {
        val iterable = charPool.find { it.contains(sourceChar) && it.contains(targetChar) }
        return findCharOrder(sourceChar, targetChar, index, iterable)
    }

    /**
     * 从字符[sourceChar]滚动变化到[targetChar]的变化顺序
     *
     * @param sourceChar 原字符
     * @param targetChar 滚动变化后的目标字符
     * @param index 字符索引
     * @param order 外部设定的序列，如果没设定则为空
     */
    open fun findCharOrder(sourceChar: Char, targetChar: Char, index: Int, order: Iterable<Char>?)
            : Pair<List<Char>, Direction> {
        return listOf(sourceChar, targetChar) to Direction.SCROLL_DOWN
    }
}

typealias CharPool = List<Collection<Char>>

/**
 * 字符动画滚动的方向：
 *
 * [SCROLL_UP] 向上滚动
 * [SCROLL_DOWN] 向下滚动
 * [SCROLL_LEFT] 向左滚动
 * [SCROLL_RIGHT] 向右滚动
 */
enum class Direction(val value: Int, val orientation: Int) {
    SCROLL_UP(-1, VERTICAL),
    SCROLL_DOWN(1, VERTICAL),

    SCROLL_LEFT(-1, HORIZONTAL),
    SCROLL_RIGHT(1, HORIZONTAL)
}