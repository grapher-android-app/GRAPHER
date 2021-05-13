package util

/*
Collection of static methods for sets
 */
object SetUtils {

    /**
     * Finds the intersecting elements of two sets
     * @param a the first set
     * @param b the second set
     * @return a set of intersecting elements
     */
    fun intersection(a : HashSet<Int>, b : HashSet<Int>) : HashSet<Int> {
        val c = HashSet<Int>(a.size.coerceAtMost(b.size))
        for (i : Int in a) {
            if (b.contains(i)) c.add(i)
        }
        return c
    }

    /**
     * Removes a given element from given set
     */
    fun setMinus(set : HashSet<Int>?, x : Int) : HashSet<Int> {
        if (set == null) return HashSet(5)
        if (!set.contains(x)) return set

        val c = HashSet<Int>(set.size)
        c.addAll(set)
        c.remove(x)
        return c
    }

    /**
     * Creates a union set of given set and given element
     */
    fun union(set : HashSet<Int>?, x : Int) : HashSet<Int> {
        if (set == null) {
            val xx = HashSet<Int>(5)
            xx.add(x)
            return xx
        }
        if (set.contains(x)) return set
        val c = HashSet<Int>(set.size + 1)
        c.addAll(set)
        c.add(x)
        return c
    }

    /**
     * Checks if a set of sets contain a given element
     */
    fun containsSetContaining(setOfSets : HashSet<HashSet<Int>>, element : Int) : Boolean {
        for (set : HashSet<Int> in setOfSets) {
            if (set.contains(element)) return true
        }
        return false
    }
}