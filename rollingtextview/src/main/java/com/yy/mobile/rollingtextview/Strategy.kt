package com.yy.mobile.rollingtextview

import com.yy.mobile.rollingtextview.TextManager.Companion.EMPTY

/**
 * Created by 张宇 on 2018/2/28.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
object Strategy {

    /**
     * 不显示动画效果
     */
    @JvmField
    val NoAnimation: CharOrderStrategy = object : SimpleCharOrderStrategy() {
        override fun findCharOrder(sourceChar: Char, targetChar: Char, index: Int, order: Iterable<Char>?) =
                listOf(targetChar) to Direction.SCROLL_DOWN
    }

    /**
     * 默认的动画效果：
     *
     * 当调用[RollingTextView.addCharOrder]之后，在*charOder*里面的顺序存在这样的关系： **【目标字符在原字符的右边】** ，
     * 则会有向下滚动的动画效果。如果 **【目标字符在原字符的左边】** ，则会有向上滚动的动画效果。如果目标字符和原字符不在同一个
     * *charOrder* 中，则不会有动画效果
     */
    @JvmField
    val NormalAnimation: CharOrderStrategy = object : SimpleCharOrderStrategy() {

        override fun findCharOrder(
                sourceChar: Char,
                targetChar: Char,
                index: Int,
                order: Iterable<Char>?): Pair<List<Char>, Direction> {

            return if (sourceChar == targetChar) {
                listOf(targetChar) to Direction.SCROLL_DOWN

            } else if (order == null) {
                listOf(sourceChar, targetChar) to Direction.SCROLL_DOWN

            } else {
                val srcIndex = order.indexOf(sourceChar)
                val tgtIndex = order.indexOf(targetChar)

                if (srcIndex < tgtIndex) {
                    order.subList(srcIndex, tgtIndex) to Direction.SCROLL_DOWN
                } else {
                    order.subList(tgtIndex, srcIndex).asReversed() to Direction.SCROLL_UP
                }
            }
        }

        private fun <T> Iterable<T>.subList(start: Int, end: Int): List<T> {
            return this.filterIndexed { index, _ -> index in start..end }
        }
    }

    /**
     * 指定方向滚动的动画
     *
     * 与默认动画效果相似，但一定会沿指定方向滚动。见[Direction]
     */
    @Suppress("FunctionName")
    @JvmStatic
    fun SameDirectionAnimation(direction: Direction): CharOrderStrategy = object : SimpleCharOrderStrategy() {

        override fun findCharOrder(
                sourceText: CharSequence,
                targetText: CharSequence,
                index: Int,
                charPool: CharPool): Pair<List<Char>, Direction> {

            return NormalAnimation.findCharOrder(sourceText, targetText, index, charPool).first to direction
        }
    }

    @JvmField
    val CarryBitAnimation: CharOrderStrategy = CarryBitStrategy()

    @JvmField
    val NonZeroFirstCarryBitAnimation: CharOrderStrategy = NonZeroFirstStrategy(CarryBitAnimation)

    @JvmStatic
    fun NonZeroFirstAnimation(orderStrategy: CharOrderStrategy): CharOrderStrategy =
            NonZeroFirstAnimation(orderStrategy)
}

@Suppress("MemberVisibilityCanBePrivate")
open class CarryBitStrategy : SimpleCharOrderStrategy() {

    protected var sourceIndex: IntArray? = null
    protected var targetIndex: IntArray? = null
    protected var sourceCumulative: IntArray? = null
    protected var targetCumulative: IntArray? = null
    protected var charOrderList: List<Collection<Char>>? = null
    protected var toBigger: Boolean = true

    override fun beforeCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {

        if (sourceText.length > 10 || targetText.length > 10) {
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
                    ?: listOf(TextManager.EMPTY)
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

    override fun afterCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {
        sourceCumulative = null
        targetCumulative = null
        charOrderList = null
        sourceIndex = null
        targetIndex = null
    }

//    override fun getFactor(sourceText: CharSequence, targetText: CharSequence, index: Int): Double {
//        //return 1.0
//        val maxLen = Math.max(sourceText.length, targetText.length)
//        return Math.pow(0.1, (maxLen - 1 - index).toDouble())
//    }

//    override fun getProgressInfo(previousProgress: PreviousProgress, index: Int): Double {
//        val charOrders = charOrderList
//        val srcIndex = sourceIndex
//        if (charOrders != null && srcIndex != null) {
//            if (index == charOrders.size - 1) {
//                return previousProgress.progress
//            }
//            val startIndex = srcIndex[index]
//            val carry = charOrders[index + 1].size
//            val charOrder = charOrders[index]
//            val currentIndex = previousProgress.currentIndex
//            if ((currentIndex + startIndex) % carry == 0) {
//
//            }
//        }
//
//        return previousProgress.progress
//    }

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
        return Strategy.NormalAnimation.findCharOrder(sourceText, targetText, index, charPool)
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

open class NonZeroFirstStrategy(private val strategy: CharOrderStrategy) : CharOrderStrategy by strategy {

    private var sourceZeroFirst = true
    private var targetZeroFirst = true

    override fun beforeCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {
        strategy.beforeCompute(sourceText, targetText, charPool)
        sourceZeroFirst = true
        targetZeroFirst = true
    }

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool
    ): Pair<List<Char>, Direction> {

        val (list, direction) = strategy.findCharOrder(sourceText, targetText, index, charPool)

        val firstIdx = firstZeroAfterEmpty(list)
        val lastIdx = lastZeroBeforeEmpty(list)
        var replaceFirst = false
        var replaceLast = false

        if (sourceZeroFirst && firstIdx != -1) {
            replaceFirst = true
        } else {
            sourceZeroFirst = false
        }

        if (targetZeroFirst && lastIdx != -1) {
            replaceLast = true
        } else {
            targetZeroFirst = false
        }

        var replaceList = if (replaceFirst && replaceLast) {
            ReplaceList(list, EMPTY, EMPTY, { firstIdx }, { lastIdx })
        } else if (replaceFirst) {
            ReplaceList(list, first = EMPTY, firstReplacePosition = { firstIdx },
                    lastReplacePosition = { lastIdx })
        } else if (replaceLast) {
            ReplaceList(list, last = EMPTY, firstReplacePosition = { firstIdx },
                    lastReplacePosition = { lastIdx })
        } else {
            list
        }

        replaceList = if (replaceFirst && replaceLast) {
            CircularList(replaceList, lastIdx - firstIdx + 1, firstIdx)
        } else if (replaceFirst) {
            CircularList(replaceList, replaceList.size - firstIdx, firstIdx)
        } else if (replaceLast) {
            CircularList(replaceList, lastIdx + 1)
        } else {
            replaceList
        }

        return replaceList to direction
    }

    private fun firstZeroAfterEmpty(list: List<Char>): Int {
        for ((idx, c) in list.withIndex()) {
            if (c == '0') {
                return idx
            }
            if (c != EMPTY) {
                break
            }
        }
        return -1
    }

    private fun lastZeroBeforeEmpty(list: List<Char>): Int {
        val iter = list.listIterator(list.size)
        var idx = list.size
        while (iter.hasPrevious()) {
            val c = iter.previous()
            idx--
            if (c == '0') {
                return idx
            }
            if (c != EMPTY) {
                break
            }
        }
        return -1
    }
}