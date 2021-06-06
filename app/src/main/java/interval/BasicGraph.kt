package interval

import model.Edge
import model.Node
import org.jgrapht.graph.SimpleGraph
import util.Coordinate
import java.lang.IllegalArgumentException
import java.lang.Integer.max
import java.lang.NullPointerException
import java.util.*
import java.util.function.Supplier
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlin.math.sqrt

class BasicGraph {

    companion object {

        fun disjointUnion(g1 : BasicGraph, g2 : BasicGraph) : BasicGraph {
            val g = g1.clone()
            var max = 0

            for (v : Int in g1.vertices) {
                max = max(max, v)
            }
            for (e : BasicEdge in g2.getEdges()) {
                g.addEdge(e.a + max, e.b + max)
            }

            g.setName("${g1.name} \\cup ${g2.name}")
            return g
        }

        fun join(g1 : BasicGraph, g2 : BasicGraph) : BasicGraph {
            val g = g1.clone()
            var offset : Int = g1.order()

            for (v : Int in g1.vertices) {
                offset = max(offset, v)
            }
            offset += 1
            for (e : BasicEdge in g2.getEdges()) {
                g.addEdge(e.a + offset, e.b + offset)
            }
            for (v : Int in g1.vertices) {
                for (u : Int in g2.vertices) {
                    g.addEdge(v, u + offset)
                }
            }

            g.setName("${g1.name} \\join ${g2.name}")
            return g
        }

        class BasicEdge(a: Int, b : Int) : Comparable<BasicEdge> {
            val a : Int
            val b : Int
            init {
                if (a < b) {
                    this.a = a
                    this.b = b
                }
                else{
                    this.a = b
                    this.b = a
                }
            }

            fun isIncident(x : Int) : Boolean {
                return x == a || x == b
            }

            fun getOther(x : Int) : Int {
                if (x == a) {
                    return b
                }
                return a
            }

            override fun compareTo(other: BasicEdge): Int {
                if (a < other.a) {
                    return -1
                }
                else if (a == other.a) {
                    if (b < other.b) {
                        return -1
                    }
                    return if (b == other.b) 0 else 1
                }
                return -1
            }

            override fun equals(other : Any?) : Boolean {
                if (other == null || (other !is BasicEdge)) {
                    return false
                }
                val edge : BasicEdge = other
                return a == edge.a && b == edge.b
            }

            override fun hashCode(): Int {
                return (a * 97) + b
            }

            override fun toString(): String {
                return "$a-$b"
            }
        }
    }

    private val vertices = HashSet<Int>()
    private val neighborhoods = HashMap<Int, HashSet<Int>>()

    private var name = ""

    fun isInterval() : Boolean {
        return isChordal() && isATFree()
    }

    private fun isATFree() : Boolean {
        return getAT() == null
    }

    fun getAT() : HashSet<Int>? {
        val vs = ArrayList<Int>(vertices)

        for (i in 0..order()) {
            for (j in i+1..order()) {
                for (k in j+1..order()) {
                    val a = vs[i]
                    val b = vs[j]
                    val c = vs[k]

                    if (isAT(a, b, c)) {
                        val s = HashSet<Int>(3)
                        s.add(a)
                        s.add(b)
                        s.add(c)
                        return s
                    }
                }
            }
        }
        return null
    }

    private fun isAT(a : Int, b : Int, c : Int) : Boolean {
        if (a == b || b == c || a == c) {
            throw IllegalArgumentException("Need three distinct vertices for AT test")
        }
        return (hasPathExceptNeigh(a, b, c)
                && hasPathExceptNeigh(a, c, b)
                && hasPathExceptNeigh(b, c, a))
    }

    fun isChordal() : Boolean {
        val g : BasicGraph = clone()
        while (g.vertices.isNotEmpty()) {
            var del = -1
            for (i : Int in g.vertices) {
                if (g.isSimplicial(i)) {
                    del = i
                    break
                }
            }
            if (del == -1) return false

            if (!g.removeVertex(del)) {
                println("Could not delete vertex $del from $g")
            }
        }
        return true
    }

    private fun isSimplicial(v : Int) : Boolean {
        return isClique(getNeighborhood(v))
    }

    // Vertex and Edge functions
    fun addVertex() : Int {
        val n = firstFreeIndex()

        addVertex(n)

        return n
    }

    fun addVertex(v : Int) : Boolean {
        if (!vertices.contains(v)) {
            vertices.add(v)
            neighborhoods[v] = HashSet(100)
        }
        return false
    }

    private fun addUniversalVertex() : Int {
        val u = addVertex()
        for (i : Int in vertices) {
            if (i != u) addEdge(i, u)
        }
        return u
    }

    fun addEdge(a : Int, b : Int) {
        if (a == b) {
            throw IllegalArgumentException("Not allowed to add self-loop on $a")
        }

        if (vertices.contains(a) && neighborhoods[a]!!.contains(b)) return

        if (!vertices.contains(a)) {
            vertices.add(a)
            val an = HashSet<Int>()
            an.add(b)
            neighborhoods[a] = an
        }
        else {
            neighborhoods[a]!!.add(b)
        }

        if (!vertices.contains(b)) {
            vertices.add(b)
            val bn = HashSet<Int>()
            bn.add(a)
            neighborhoods[b] = bn
        }
        else {
            neighborhoods[b]!!.add(a)
        }
    }

    fun removeVertex(v : Int) : Boolean {
        if (!vertices.contains(v)) return false

        val neigh : HashSet<Int> = neighborhoods[v]!!

        for (nv : Int in neigh) {
            neighborhoods[nv]!!.remove(v)
        }

        vertices.remove(v)
        neighborhoods.remove(v)

        return true
    }

    // changed to HashSet as only use case and Collection lacks remove method
    fun removeVertices(delete : HashSet<Int>) : Boolean {
        var result = false
        while (delete.isNotEmpty()) {
            val v : Int = delete.iterator().next()
            if (vertices.contains(v)) {
                result = true
                removeVertex(v)
            }
            delete.remove(v)
        }
        return result
    }

    fun getEdges() : Collection<BasicEdge> {
        val s = HashSet<BasicEdge>()
        for (neigh in neighborhoods.entries) {
            val v : Int = neigh.key
            for (nv : Int in neigh.value) {
                s.add(BasicEdge(v, nv))
            }
        }
        return s
    }

    fun getVertices() : HashSet<Int> {
        val s = HashSet<Int>()
        for (v : Int in vertices) {
            s.add(v)
        }
        return s
    }

    // functions relating to object information
    fun clone() : BasicGraph {
        val gc = BasicGraph()
        for (v : Int in vertices) {
            gc.addVertex(v)
        }

        for (v : Int in neighborhoods.keys) {
            for (nv : Int in neighborhoods[v]!!) {
                gc.addEdge(v, nv)
            }
        }
        gc.name = name
        return gc
    }

    /**
     * Returns if b is a part of a's neighborhood
     */
    private fun isAdjacent(a : Int, b : Int) : Boolean {
        return neighborhoods[a]!!.contains(b)
    }

    fun isMaximalCique(s : Collection<Int>) : Boolean {
        if (!isClique(s)) return false

        val nonMembers = HashSet<Int>(order())
        for (v : Int in vertices) {
            if (!s.contains(v)) nonMembers.add(v)
        }

        for (v : Int in nonMembers) {
            val potential = HashSet<Int>(s.size + 1)
            potential.addAll(s)
            potential.add(v)
            if (isClique(potential)) return false
        }
        return true
    }

    private fun isClique(s : Collection<Int>?) : Boolean {
        if (s == null) {
            throw NullPointerException("Clique input was null")
        }
        if (s.size <= 1) return true

        if (s.size == 2) {
            val it : Iterator<Int> = s.iterator()
            val a : Int = it.next()
            val b : Int = it.next()
            return isAdjacent(a, b)
        }

        val x : IntArray = s.toIntArray()
        for (i in 0..x.size) {
            for (j in i+1..x.size) {
                if (!isAdjacent(x[i], x[j])) return false
            }
        }
        return true
    }

    fun isConnected() : Boolean {
        if (vertices.size <= 1 || getEdges().isEmpty()) return false

        val s = vertices.iterator().next()
        for (v : Int in vertices) {
            if (shortestPath(s, v) < 0) return false
        }
        return true
    }

    private fun firstFreeIndex() : Int {
        var ffi = 1
        for (i : Int in vertices) {
            ffi = max(ffi, i + 1)
        }
        return ffi
    }

    fun getName() : String {
        return name
    }

    fun setName(name : String) {
        this.name = name
    }

    fun getClosedNeighborhood(v : Int) : HashSet<Int> {
        val n = getNeighborhood(v)
        n.add(v)
        return n
    }

    private fun getNeighborhood(v : Int) : HashSet<Int> {
        val n = HashSet<Int>()
        for (i : Int in neighborhoods[v]!!) {
            n.add(i)
        }
        return n
    }

    fun order() : Int {
        return vertices.size
    }

    private fun hasPathExceptNeigh(a : Int, b : Int, c : Int) : Boolean {
        if (isAdjacent(a, c) || isAdjacent(b, c)) {
            return false
        }
        if (isAdjacent(a, b)) return true

        val gMinusNc = clone()
        gMinusNc.removeVertices(getNeighborhood(c))
        return gMinusNc.shortestPath(a, b) >= 0
    }

    /**
     * Returns length of shortest path or -1 if disconnceted
     */
    private fun shortestPath(a : Int, b : Int) : Int {
        if (!vertices.contains(a) || vertices.contains(b)) {
            throw IllegalArgumentException("Not vertices $a, $b")
        }
        if (a == b) return 0
        if (isAdjacent(a, b)) return 1

        val dist = HashMap<Int, Int>(order())
        val q = LinkedList<Int>()
        q.add(a)
        dist[a] = 0

        while (q.isNotEmpty()) {
            val x = q.poll()
            if (x != null) {
                for (v : Int in getNeighborhood(x)) {
                    if (!dist.containsKey(v)) {
                        q.add(v)
                        dist[v] = dist[x]!!.plus(1)
                        if (v == b) return dist[v]!!
                    }
                }
            }
        }
        return -1
    }

    fun getSimpleGraph(edgeSupplier : Supplier<Edge<Node>>) : SimpleGraph<Node, Edge<Node>> {
        val n = vertices.size
        val sg = SimpleGraph<Node, Edge<Node>>(null, edgeSupplier, false)
        val map = HashMap<Int, Node>()

        for (i : Int in vertices) {
            val x : Float = (i % (sqrt(n.toDouble()).toInt() + 1)).toFloat()
            val y : Float = i - x
            val ni = Node(Coordinate(x, y))
            map[i] = ni
        }

        for (a : Int in vertices) {
            for (b : Int in neighborhoods[a]!!) {
                if (!sg.containsEdge(map[a], map[b])) {
                    val edge = sg.addEdge(map[a], map[b])
                    edge.setSource(map[a]!!)
                    edge.setTarget(map[b]!!)
                }
            }
        }
        return sg
    }
}