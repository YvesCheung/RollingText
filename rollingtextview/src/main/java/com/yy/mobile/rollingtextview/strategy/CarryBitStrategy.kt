package com.yy.mobile.rollingtextview.strategy

import com.yy.mobile.rollingtextview.*
import com.yy.mobile.rollingtextview.util.CircularList
import com.yy.mobile.rollingtextview.util.ExtraList

/**
 * Created by 张宇 on 2018/3/4.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */

@Suppress("MemberVisibilityCanBePrivate")
open class CarryBitStrategy : SimpleCharOrderStrategy() {

    protected var sourceIndex: IntArray? = null
    protected var targetIndex: IntArray? = null
    protected var sourceCumulative: IntArray? = null
    protected var targetCumulative: IntArray? = null
    protected var charOrderList: List<Collection<Char>>? = null
    protected var toBigger: Boolean = true

    override fun beforeCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {

        if (sourceText.length >= 10 || targetText.length >= 10) {
            throw IllegalStateException("your text is too long, it may overflow the integer calculation." +
                    " please use other animation strategy.")
        }

        val maxLen = Math.max(sourceText.length, targetText.length)
        val srcArray = IntArray(maxLen)
        val tgtArray = IntArray(maxLen)
        val carryArray = IntArray(maxLen)
        val charOrderList = mutableListOf<Collection<Char>>()
        (0 until maxLen).forEach { index ->
            var srcChar = TextManager.EMPTY
            var tgtChar = TextManager.EMPTY
            val sIdx = index - maxLen + sourceText.length
            val tIdx = index - maxLen + targetText.length
            if (sIdx >= 0) {
                srcChar = sourceText[sIdx]
            }
            if (tIdx >= 0) {
                tgtChar = targetText[tIdx]
            }
            val iterable = charPool.find { it.contains(srcChar) && it.contains(tgtChar) }
                    ?: throw IllegalStateException("the char $srcChar or $tgtChar cannot be found in the charPool," +
                            "please addCharOrder() before use")
            charOrderList.add(iterable)
            srcArray[index] = Math.max(iterable.indexOf(srcChar) - 1, -1)
            tgtArray[index] = Math.max(iterable.indexOf(tgtChar) - 1, -1)
            carryArray[index] = iterable.size - 1
        }

        val sourceCumulative = IntArray(maxLen)
        val targetCumulative = IntArray(maxLen)
        var srcTotal = 0
        var tgtTotal = 0
        var carry = 0
        (0 until maxLen).forEach { idx ->
            srcTotal = Math.max(srcArray[idx], 0) + carry * srcTotal
            tgtTotal = Math.max(tgtArray[idx], 0) + carry * tgtTotal
            carry = carryArray[idx]
            sourceCumulative[idx] = srcTotal
            targetCumulative[idx] = tgtTotal
        }

        this.sourceIndex = srcArray
        this.targetIndex = tgtArray
        this.sourceCumulative = sourceCumulative
        this.targetCumulative = targetCumulative
        this.charOrderList = charOrderList
        this.toBigger = srcTotal < tgtTotal
    }

    override fun afterCompute() {
        sourceCumulative = null
        targetCumulative = null
        charOrderList = null
        sourceIndex = null
        targetIndex = null
    }

    override fun nextProgress(
            previousProgress: PreviousProgress,
            columnIndex: Int,
            columns: List<List<Char>>,
            charIndex: Int): NextProgress {

        /**
         * 最低位 线性变化
         */
        if (columnIndex == columns.size - 1) {
            return super.nextProgress(previousProgress, columnIndex, columns, charIndex)
        }

        val srcIndex = sourceIndex
        val charOrders = charOrderList
        if (srcIndex != null && charOrders != null) {
            val preStartIndex = Math.max(srcIndex[columnIndex + 1], 0)
            val preCarry = charOrders[columnIndex + 1].size - 1
            val preCurrentIndex = previousProgress.currentIndex
            val nextStartIndex = if (toBigger) {
                (preCurrentIndex + preStartIndex) / preCarry
            } else {
                (preCurrentIndex - preStartIndex - 1 + preCarry) / preCarry
            }
            val upgrade = if (toBigger) {
                (preCurrentIndex + preStartIndex + 1) % preCarry == 0
            } else {
                (preCurrentIndex - preStartIndex) % preCarry == 0
            }
            return if (upgrade) {
                NextProgress(nextStartIndex, previousProgress.offsetPercentage, previousProgress.progress)
            } else {
                NextProgress(nextStartIndex, 0.0, previousProgress.progress)
            }
        }
        return super.nextProgress(previousProgress, columnIndex, columns, charIndex)
    }

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool
    ): Pair<List<Char>, Direction> {

        val srcIndex = sourceIndex
        val tgtIndex = targetIndex
        val srcCumulate = sourceCumulative
        val tgtCumulate = targetCumulative
        val charOrders = charOrderList
        if (srcCumulate != null && tgtCumulate != null
                && charOrders != null && srcIndex != null && tgtIndex != null) {

            val orderList = charOrders[index].filterIndexed { i, _ -> i > 0 }

            val size = Math.abs(srcCumulate[index] - tgtCumulate[index]) + 1
            var first: Char? = null
            var last: Char? = null
            if (srcIndex[index] == -1) first = TextManager.EMPTY
            if (tgtIndex[index] == -1) last = TextManager.EMPTY
            val (list, firstIndex) = determineCharOrder(orderList, Math.max(srcIndex[index], 0))
            return circularList(
                    rawList = list,
                    size = size,
                    firstIndex = firstIndex,
                    first = first,
                    last = last
            ) to determineDirection()
        }
        throw IllegalStateException("CarryBitStrategy is in a illegal state, check it's lifecycle")
    }

    open fun circularList(
            rawList: List<Char>,
            size: Int,
            firstIndex: Int,
            first: Char?,
            last: Char?): List<Char> {
        val circularList = CircularList(rawList, size, firstIndex)
        return ExtraList(circularList, first, last)
    }

    open fun determineCharOrder(orderList: List<Char>, index: Int): Pair<List<Char>, Int> {
        return if (toBigger) {
            orderList to index
        } else {
            orderList.asReversed() to (orderList.size - 1 - index)
        }
    }

    open fun determineDirection(): Direction = if (toBigger) Direction.SCROLL_DOWN else Direction.SCROLL_UP
}