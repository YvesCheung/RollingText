package com.yy.mobile.rollingtextview.util

/**
 * Created by 张宇 on 2018/3/4.
 * E-mail: zhangyu4@yy.com
 * YY: 909017428
 */
class ReplaceList<T>(
        val list: List<T>,
        val first: T? = null,
        val last: T? = null,
        firstReplacePosition: () -> Int,
        lastReplacePosition: () -> Int
) : List<T> {

    override val size: Int = list.size

    private var firstIdx = -1
    private var lastIdx = -1

    init {
        if (first != null) {
            firstIdx = firstReplacePosition()
        }
        if (last != null) {
            lastIdx = lastReplacePosition()
        }
    }

    override fun contains(element: T): Boolean = any { it == element }

    override fun containsAll(elements: Collection<T>): Boolean = elements.all { contains(it) }

    override fun get(index: Int): T {
        return when {
            index == firstIdx && first != null -> first
            index == lastIdx && last != null -> last
            else -> list[index]
        }
    }

    override fun indexOf(element: T): Int = indexOfFirst { it == element }

    override fun isEmpty(): Boolean = size <= 0

    override fun iterator(): Iterator<T> = ReplaceIterator()

    override fun lastIndexOf(element: T): Int = indexOfLast { it == element }

    override fun listIterator(): ListIterator<T> = ReplaceIterator()

    override fun listIterator(index: Int): ListIterator<T> = ReplaceIterator(index)

    override fun subList(fromIndex: Int, toIndex: Int): List<T> {
        throw IllegalStateException("Not support")
    }

    private inner class ReplaceIterator(private var index: Int = 0) : ListIterator<T> {

        init {
            if (index < 0 || index > size) {
                throw ArrayIndexOutOfBoundsException("index should be in range [0,$size] but now is $index")
            }
        }

        override fun hasNext() = index < size

        override fun hasPrevious() = index > 0

        override fun next(): T {
            if (!hasNext()) throw NoSuchElementException()
            return get(index++)
        }

        override fun nextIndex(): Int = index

        override fun previous(): T {
            if (!hasPrevious()) throw NoSuchElementException()
            return get(--index)
        }

        override fun previousIndex(): Int = index - 1
    }
}