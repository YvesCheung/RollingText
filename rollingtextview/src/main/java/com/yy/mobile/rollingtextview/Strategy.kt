package com.yy.mobile.rollingtextview

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
    fun SameDirectionAnimation(direction: Direction): CharOrderStrategy = object : CharOrderStrategy {

        override fun findCharOrder(
                sourceText: CharSequence,
                targetText: CharSequence,
                index: Int,
                charPool: CharPool): Pair<List<Char>, Direction> {

            return NormalAnimation.findCharOrder(sourceText, targetText, index, charPool).first to direction
        }
    }

    @JvmField
    val CarryBitAnimation: CharOrderStrategy = object : CharOrderStrategy {

        var sourceIndex: IntArray? = null
        var targetIndex: IntArray? = null
        var sourceCumulative: IntArray? = null
        var targetCumulative: IntArray? = null
        var charOrderList: List<Collection<Char>>? = null
        var direction: Direction = Direction.SCROLL_DOWN

        override fun beforeCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {

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
            this.direction = if (srcTotal > tgtTotal) Direction.SCROLL_UP else Direction.SCROLL_DOWN
        }

        override fun afterCompute(sourceText: CharSequence, targetText: CharSequence, charPool: CharPool) {
            sourceCumulative = null
            targetCumulative = null
            charOrderList = null
            sourceIndex = null
            targetIndex = null
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
                return extraCircularList(
                        if (direction == Direction.SCROLL_UP)
                            orderList.asReversed()
                        else orderList,
                        size,
                        Math.max(srcIndex[index], 0),
                        first,
                        last
                ) to direction
            }
            return NormalAnimation.findCharOrder(sourceText, targetText, index, charPool)
        }

        private fun <T> extraCircularList(
                rawList: List<T>,
                size: Int,
                firstIndex: Int,
                first: T? = null,
                last: T? = null): List<T> {
            val circularList = CircularList(rawList, size, firstIndex)
            return ExtraList(circularList, first, last)
        }
    }
}