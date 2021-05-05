package util

import java.math.BigInteger
import java.util.*

/**
 * Given a collection of elements of type T, gives, one by one, every
 * permutation of it. What is a permutation? It is a choice of first element,
 * followed by a permutation of the rest of the elements.
 *
 * Use the method hasNext to check if there are more permutations to be given.
 * Use the method next to get the next permutation. If next is used when hasNext
 * returns false, it will give a permutation already given before.
 *
 * Notice that it will assume each element of the collection distingth, no
 * matter what equals says. So with a collection of exactly 4 equal elements it
 * will give 24 equal permutations.
 *
 * @author Erik Parmann
 *
 * @param <T>
 * The type of the elements that should be permutated.
</T> */
/*
 * This idea behind this implementation is as follows. We have the elements in
 * some "canonical" order, here the order they are in the collection we get.
 * Then each permutation can be described as a list of integers, where the first
 * integer gives which element from the canonical ordering should be the first
 * element. The second integer gives where in the canonical ordering of the
 * remaining elements we can find the second element of the permutation, and so
 * on.
 *
 * In this implementation the list of integers [1, 1, 1] would mean that the
 * first element in the permutation is the first element in the original
 * collection. The second element in the permutation is the first of the
 * remaining elements, so therefore the second element, and so on.
 *
 * The construction of a permutation goes through two faces. The first is to
 * iterate the list of integers that represent the permutation, and the second
 * phase is to reconstruct the actual permutation. The second can maybe be
 * optimized a bit, as it is O(n^2), because of the removal of a specific index
 * in an ArrayList.
 */
class PermutationIterator<T>(elements: Collection<T>?) : MutableIterator<Collection<T>?> {
    private val elems: ArrayList<T>
    private val currentPermutation: IntArray

    // To indicate whether we have delivered the first permutation (and are
    // possibly on our second round).
    private var deliveredFirstOnce = false

    /*
	 * This method will iterate the integer-representation of the permutation by
	 * one.
	 */
    private fun iterateCurrentPermutation() {
        var possibleElementsAtIndex = 1
        for (i in currentPermutation.indices.reversed()) {
            currentPermutation[i] = ((currentPermutation[i] + 1)
                    % possibleElementsAtIndex)
            // When we have exchausted all possible elements at this position we
            // are done.
            if (currentPermutation[i] != 0) {
                break
            }
            possibleElementsAtIndex++
        }
    }

    private fun generatePermutation(): ArrayList<T> {
        val copyOfelems = ArrayList(elems)
        val returnPermutation = ArrayList<T>(elems.size)
        for (i in currentPermutation.indices) {
            returnPermutation.add(copyOfelems.removeAt(currentPermutation[i]))
        }
        return returnPermutation
    }

    override fun hasNext(): Boolean {
        for (i in currentPermutation.indices) {
            if (currentPermutation[i] != 0) {
                return true
            }
        }
        // Only false if we have delivered the first permutation at least once.
        return false || !deliveredFirstOnce
    }

    override fun next(): ArrayList<T> {
        deliveredFirstOnce = true
        iterateCurrentPermutation()
        return generatePermutation()
    }

    override fun remove() {
        throw IllegalArgumentException()
    }

    companion object {
        /**
         * Returns n! if 0 <= n <= 12, returns -1 if n > 12, throws exception on
         * negative numbers.
         *
         * @param n
         * integer
         * @return n! or -1 if n too large
         */
        fun factorial(n: Int): Int {
            if (n >= 13) return -1
            require(n >= 0) { "factorial(n) only defined for positive numbers, not $n" }
            return if (n <= 1) 1 else factorial(n - 1) * n
        }

        /**
         * Returns n!
         *
         * @param n
         * biginteger
         * @return n!
         */
        fun factorial(n: BigInteger): BigInteger {
            return if (n == BigInteger.ONE) {
                BigInteger.ONE
            } else {
                val pn = n.subtract(BigInteger.ONE)
                n.multiply(pn)
            }
        }
    }

    init {
        elems = ArrayList(elements)
        currentPermutation = IntArray(elems.size)
    }
}