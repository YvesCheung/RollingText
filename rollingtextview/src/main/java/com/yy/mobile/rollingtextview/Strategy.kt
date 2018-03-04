@file:Suppress("FunctionName")

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
    @JvmStatic
    fun NoAnimation(): CharOrderStrategy = object : SimpleCharOrderStrategy() {
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
    @JvmStatic
    fun NormalAnimation(): CharOrderStrategy = NormalAnimationStrategy()

    /**
     * 指定方向滚动的动画
     *
     * 与默认动画效果相似，但一定会沿指定方向滚动。见[Direction]
     */
    @JvmStatic
    fun SameDirectionAnimation(direction: Direction): CharOrderStrategy = SameDirectionStrategy(direction)

    @JvmStatic
    fun CarryBitAnimation(): CharOrderStrategy = NonZeroFirstStrategy(CarryBitStrategy())

    @JvmStatic
    fun NonZeroFirstAnimation(orderStrategy: CharOrderStrategy): CharOrderStrategy =
            NonZeroFirstStrategy(orderStrategy)
}