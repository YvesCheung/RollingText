package com.yy.mobile.rollingtextview

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
     * 从[sourceText]滚动变化到[targetText]，对于索引[index]的位置，给出变化的字符顺序
     *
     * 也可以直接继承[SimpleCharOrderStrategy]，可以更简单的实现策略
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
}

/**
 * 简单的策略模版
 * a simple strategy template
 */
abstract class SimpleCharOrderStrategy : CharOrderStrategy {

    override fun findCharOrder(
            sourceText: CharSequence,
            targetText: CharSequence,
            index: Int,
            charPool: CharPool): Pair<List<Char>, Direction> {

        val maxLen = Math.max(sourceText.length, targetText.length)
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

        val iterable = charPool.find { it.contains(srcChar) && it.contains(tgtChar) }
        return findCharOrder(srcChar, tgtChar, iterable)
    }

    /**
     * 从字符[sourceChar]滚动变化到[targetChar]的变化顺序
     *
     * @param sourceChar 原字符
     * @param targetChar 滚动变化后的目标字符
     * @param order 外部设定的序列，如果没设定则为空
     */
    abstract fun findCharOrder(sourceChar: Char, targetChar: Char, order: Iterable<Char>?): Pair<List<Char>, Direction>
}

typealias CharPool = List<Iterable<Char>>

enum class Direction(var value: Int) {
    SCROLL_UP(-1),
    SCROLL_DOWN(1)
}