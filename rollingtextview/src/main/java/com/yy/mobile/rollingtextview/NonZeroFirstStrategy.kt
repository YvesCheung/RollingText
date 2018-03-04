package com.yy.mobile.rollingtextview

/**
 * Created by 张宇 on 2018/3/4.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
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

        val maxLen = Math.max(sourceText.length, targetText.length)
        val firstIdx = firstZeroAfterEmpty(list)
        val lastIdx = lastZeroBeforeEmpty(list)
        var replaceFirst = false
        var replaceLast = false

        if (sourceZeroFirst && firstIdx != -1 && index != maxLen - 1) {
            replaceFirst = true
        } else {
            sourceZeroFirst = false
        }

        if (targetZeroFirst && lastIdx != -1 && index != maxLen - 1) {
            replaceLast = true
        } else {
            targetZeroFirst = false
        }

        var replaceList = if (replaceFirst && replaceLast) {
            ReplaceList(list, TextManager.EMPTY, TextManager.EMPTY, { firstIdx }, { lastIdx })
        } else if (replaceFirst) {
            ReplaceList(list, first = TextManager.EMPTY, firstReplacePosition = { firstIdx },
                    lastReplacePosition = { lastIdx })
        } else if (replaceLast) {
            ReplaceList(list, last = TextManager.EMPTY, firstReplacePosition = { firstIdx },
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
            if (c != TextManager.EMPTY) {
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
            if (c != TextManager.EMPTY) {
                break
            }
        }
        return -1
    }
}