package com.yy.mobile.rollingtextview.strategy

/**
 * Created by 张宇 on 2018/3/4.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
open class NormalAnimationStrategy : SimpleCharOrderStrategy() {

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