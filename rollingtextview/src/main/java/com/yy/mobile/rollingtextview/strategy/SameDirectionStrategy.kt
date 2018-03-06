package com.yy.mobile.rollingtextview.strategy

/**
 * Created by 张宇 on 2018/3/5.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class SameDirectionStrategy(
        private val direction: Direction,
        private val otherStrategy: CharOrderStrategy = Strategy.NormalAnimation()
) : SimpleCharOrderStrategy() {

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool): Pair<List<Char>, Direction> {

        return otherStrategy.findCharOrder(sourceText, targetText, index, charPool).first to direction
    }
}