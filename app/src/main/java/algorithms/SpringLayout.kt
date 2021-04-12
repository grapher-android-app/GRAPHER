package algorithms

import model.Edge
import model.Node
import org.jgrapht.alg.connectivity.ConnectivityInspector
import org.jgrapht.graph.SimpleGraph
import settings.Geometric
import util.Coordinate
import java.util.function.Supplier

/**
 * A class designed to "normalize" the graph into its
 * most simple representation when the device is shaken
 */
class SpringLayout(private var graph : SimpleGraph<Node, Edge<Node>>) {
    companion object {
        /** This spring's constant, ref Hooke's law */
        const val SPRING_CONSTANT : Float = .000002f // test 1

        /** How much time between iterations */
        const val TIME_CONSTANT : Float = 400f // test 300

        /**
         * The most a vertex is allowed to move during an iteration.
         * If net force is greater we scale it down to this value.
         */
        const val MAX_MOVEMENT : Float = 50f
    }

    // placeholder for not functional edge-supplier
    private var s1 = SpringNode(Node(Coordinate(0f, 0f)), 1)
    private var s2 = SpringNode(Node(Coordinate(0f, 0f)), 2)
    private var edgeSup = Supplier { Edge(s1, s2) }
    private var layout : SimpleGraph<SpringNode, Edge<SpringNode>> =
            SimpleGraph(null, edgeSup, false)

    var nodeToComponent = HashMap<Node, Int>()

    private val fromGraphToLayout = HashMap<Geometric, SpringNode>()
    private val fromLayoutToGraph = HashMap<SpringNode, Geometric>()

    init {
        initialize()
    }

    fun iterate() {
        preprocess()
        doOneIteration()
        copyPositions()
    }

    fun iterate(n : Int) {
        preprocess()
        for (i in 0..n) {
            doOneIteration()
        }
        copyPositions()
    }

    private fun doOneIteration() {
        calculateRepulsion()
        calculateTension()
        move()
        resetNetForce()
    }

    private fun resetNetForce() {
        for (sn : SpringNode in layout.vertexSet()) {
            sn.netForce = Coordinate.ZERO
        }
    }

    private fun move() {
        for (sn : SpringNode in layout.vertexSet()) {
            if (sn.netForce.length() > MAX_MOVEMENT) {
                val unit : Coordinate = sn.netForce.normalize()
                sn.netForce = unit.multiply(MAX_MOVEMENT)
            }
            sn.position = sn.position.add(sn.netForce)
            sn.position = sn.position.rounded()
        }
    }

    private fun calculateRepulsion() {
        for (sn : SpringNode in layout.vertexSet()) {
            for (sm : SpringNode in layout.vertexSet()) {
                if (sm != sn && sm.sameComponent(sn)) {
                    val coordSn : Coordinate = sn.position
                    val coordSm : Coordinate = sm.position
                    val force = repulsion(coordSn, coordSm)
                    sn.netForce = sn.netForce.add(force)
                }
            }
        }
    }

    /**
     * Calculates how much two adjacent nodes attract each other.
     * Uses Hooke's law with SPRING_CONSTANT
     */
    private fun calculateTension() {
        for (edge : Edge<SpringNode> in layout.edgeSet()) {
            val source : SpringNode = edge.getSource()
            val target : SpringNode = edge.getTarget()

            val sourcePos = source.position
            val targetPos = target.position

            val force : Coordinate = tension(sourcePos, targetPos)
            val inverseForce : Coordinate = force.inverse()

            source.netForce = source.netForce.add(force)
            target.netForce = target.netForce.add(inverseForce)
        }
    }

    private fun initialize() {
        fromGraphToLayout.clear()
        fromLayoutToGraph.clear()
        nodeToComponent.clear()

        // computes which connected components the different nodes belong to
        val connectInspector = ConnectivityInspector<Node, Edge<Node>>(graph)
        val connectedSets : List<Set<Node>> = connectInspector.connectedSets()
        for (i in connectedSets.indices) {
            for (n : Node in connectedSets[i]) {
                nodeToComponent[n] = i + 1
            }
        }

        layout = SimpleGraph<SpringNode, Edge<SpringNode>>(null, edgeSup, false)

        // fills the hashsets with easy reference to and from the real graph and SpringLayout
        for (n : Geometric in graph.vertexSet()) {
            val sn = SpringNode(n, nodeToComponent[n]!!)
            layout.addVertex(sn)
            fromGraphToLayout[n] = sn
            fromLayoutToGraph[sn] = n
        }

        for (edge : Edge<Node> in graph.edgeSet()) {
            val source : Geometric = edge.getSource()
            val target : Geometric = edge.getTarget()
            // Supplier workout as usual
            val edge = Edge(fromGraphToLayout[source]!!, fromGraphToLayout[target]!!)
            layout.addEdge(fromGraphToLayout[source], fromGraphToLayout[target], edge)
        }
    }

    private fun preprocess() {
        initialize()
        for (sn : SpringNode in layout.vertexSet()) {
            val gn : Geometric = fromLayoutToGraph[sn]!!
            sn.position = gn.getCoordinate().rounded()
        }
    }

    private fun tension(a : Coordinate, b : Coordinate)  : Coordinate {
        val forceDirection : Coordinate = a.moveVector(b)
        val dist : Float = a.distance(b)
        val scalar : Float = SPRING_CONSTANT * dist
        return forceDirection.multiply(scalar).multiply(TIME_CONSTANT)
    }

    private fun repulsion(a : Coordinate, b : Coordinate) : Coordinate {
        var dist : Float = a.distance(b)
        if (dist == 0f) {
            dist = 0.001f
        }
        val scalar : Float = 1f / (dist * dist)
        val forceDirection = a.moveVector(b).inverse()
        return forceDirection.multiply(scalar).multiply(TIME_CONSTANT)
    }

    private fun copyPositions() {
        for (n : Geometric in layout.vertexSet()) {
            fromLayoutToGraph[n]!!.setCoordinate(n.getCoordinate())
        }
    }

    inner class SpringNode(val node : Geometric, val component : Int) : Geometric {
        var position : Coordinate = node.getCoordinate()
        var netForce = Coordinate.ZERO

        override fun getCoordinate(): Coordinate {
            return position
        }

        override fun setCoordinate(coordinate: Coordinate) {
            this.position = coordinate
        }

        fun sameComponent(other : SpringNode) : Boolean {
            return component == other.component
        }
    }
}
