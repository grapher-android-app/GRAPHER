package util

import model.PermutationIterator
import java.math.BigInteger
import java.util.*

class NChooseKIterator<T>(elts: Collection<T>, private val k: Int) : MutableIterator<Collection<T>?> {
    private val elements: ArrayList<T>
    private val characteristic: BooleanArray
    private val n: Int = elts.size
    private var hasnext = true
    override fun hasNext(): Boolean {
        // if all the true's are in the k last positions, there are no next!
        return hasnext
    }

    override fun next(): Collection<T> {
        val ret = HashSet<T>(k)
        for (i in 0 until n) {
            if (characteristic[i]) ret.add(elements[i])
        }
        if (hasNext()) donext()
        return ret
    }

    private fun donext() {
        // find first 0 (from right)
        // // find first 1 after this
        // // // move to right
        // // // take every 1 right of this all the way to left (but after 1)
        var firstFromRightFalse = n
        var numberOfTruesToRightOfFirstFalse = 0
        for (i in n - 1 downTo 0) {
            if (!characteristic[i]) {
                firstFromRightFalse = i
                break
            } else {
                numberOfTruesToRightOfFirstFalse++
                characteristic[i] = false
            }
        }
        if (firstFromRightFalse == n) {
            // only happens if there are only 1's, i.e. n == k
            hasnext = false
            return
        }
        var firstFromRightTrue = n
        for (i in firstFromRightFalse downTo 0) {
            if (characteristic[i]) {
                firstFromRightTrue = i
                break
            }
        }
        if (firstFromRightTrue == n) {
            hasnext = false
            return
        }
        characteristic[firstFromRightTrue] = false
        characteristic[firstFromRightTrue + 1] = true
        for (i in 0 until numberOfTruesToRightOfFirstFalse) {
            characteristic[firstFromRightTrue + 2 + i] = true
        }
    }

    override fun remove() {}

    companion object {
        fun nChooseK(n: Int, k: Int): Int {
            require(k <= n) {
                ("N Choose K must have k <= n: "
                        + k + " " + n)
            }
            val numerator: Int = PermutationIterator.factorial(n)
            val denomerator: Int = PermutationIterator.factorial(n - k)
            return numerator / denomerator
        }

        fun nChooseK(n: BigInteger, k: BigInteger): BigInteger {
            require(k.compareTo(n) <= 0) {
                ("N Choose K must have k <= n: "
                        + k + " " + n)
            }
            val numerator: BigInteger = PermutationIterator.factorial(n)
            val denomerator: BigInteger = PermutationIterator.factorial(n.subtract(k))
            return numerator.divide(denomerator)
        }
    }

    init {
        require(n >= this.k) {
            ("n choose k demands to have n >= k, you had n="
                    + elts.size + ", k=" + k)
        }
        elements = ArrayList(elts.size)
        elements.addAll(elts)
        characteristic = BooleanArray(n)
        for (i in 0 until k) {
            characteristic[i] = true
        }
    }
}