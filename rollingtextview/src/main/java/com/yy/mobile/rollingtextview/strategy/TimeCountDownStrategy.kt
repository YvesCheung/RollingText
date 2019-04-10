package com.yy.mobile.rollingtextview.strategy

/**
 * Created by qiu on 2018/10/10.
 * E-mail: jianqiu.sysu@gmail.com
 * YY: 909017428
 */
open class TimeCountDownStrategy(private val direction: Direction) : SimpleCharOrderStrategy() {

    override fun findCharOrder(
            sourceChar: Char,
            targetChar: Char,
            index: Int,
            order: Iterable<Char>?): Pair<List<Char>, Direction> {

        return if (sourceChar == targetChar) {
            listOf(targetChar) to direction

        } else listOf(sourceChar, targetChar) to direction
    }
}