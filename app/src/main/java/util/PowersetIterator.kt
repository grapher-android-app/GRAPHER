package util

/**
 *
 * @author pgd
 *
 * @param <T>
</T> */
class PowersetIterator<T>(input: Collection<T>) : MutableIterator<Collection<T>?> {
    private val n: Int
    private var k = 0
    private val set: MutableList<T>
    private var currentIterator: NChooseKIterator<T>
    override fun hasNext(): Boolean {
        return k < n || currentIterator.hasNext()
    }

    override fun next(): Collection<T> {
        if (currentIterator.hasNext()) return currentIterator.next()
        currentIterator = NChooseKIterator<T>(set, ++k)
        return currentIterator.next()
    }

    override fun remove() {
        throw UnsupportedOperationException("Cannot remove a set using this iterator")
    }

    class PowersetIteratorDescending<S>(input: Collection<S>) : MutableIterator<Collection<S>?> {
        private val n: Int
        private var k = 0
        private val set: MutableList<S>
        private var currentIterator: NChooseKIterator<S>
        override fun hasNext(): Boolean {
            return k > 0 || currentIterator.hasNext()
        }

        override fun next(): Collection<S> {
            if (currentIterator.hasNext()) return currentIterator.next()
            currentIterator = NChooseKIterator<S>(set, --k)
            return currentIterator.next()
        }

        override fun remove() {
            throw UnsupportedOperationException("Cannot remove a set using this iterator")
        }

        init {
            set = ArrayList(input.size)
            set.addAll(input)
            n = set.size
            k = n
            currentIterator = NChooseKIterator<S>(set, k)
        }
    }

    companion object {
        fun twoPower(n: Int): Int {
            if (n > 31) return -1
            require(n >= 0) { "Undefined on negative numbers, $n" }
            return if (n == 0) 1 else 2 * twoPower(n - 1)
        }
    }

    init {
        set = ArrayList(input.size)
        set.addAll(input)
        n = set.size
        currentIterator = NChooseKIterator<T>(set, k)
    }
}