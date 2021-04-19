package util

import model.Edge
import model.Node
import org.jgrapht.graph.SimpleGraph
import java.util.*
import kotlin.collections.ArrayList

class Undo(var graph : SimpleGraph<Node, Edge<Node>>) {

    // History = Action performed
    private val history = Stack<History>()
    @Volatile var hasChanged = true

    fun graphChangedSinceLastCheck() : Boolean {
        // Basic INF214 volatile variable stuff
        val temp = hasChanged
        hasChanged = false
        return temp
    }

    fun clear(graph : SimpleGraph<Node, Edge<Node>>) {
        this.graph = graph
        hasChanged = false
        history.clear()
    }

    fun undo(): Boolean {
        if (history.isEmpty()) {
            return false
        }
        val action : History = history.pop()
        // if the last action was adding a node
        if (action.isNode) {
            // if the last action was add -> re-delete it
            if (action.add) {
                graph.removeVertex(action.node)
            }
            // if the last action was delete -> re-add it
            else {
                graph.addVertex(action.node)
                for (node in action.neighbors) {
                    graph.addEdge(action.node, node)
                }
            }
        }
        // if the last action was adding an edge
        else {
            // if the last action was add -> re-delete it
            if (action.add) {
                graph.removeEdge(action.node, action.otherNode)
            }
            // if the last action was delete -> re-add it
            else {
                graph.addEdge(action.node, action.otherNode)
            }
        }
        hasChanged = true
        return true
    }

    fun addVertex(node : Node) : Boolean {
        addHistory(node, true)
        return graph.addVertex(node)
    }

    fun removeVertex(node : Node) : Boolean {
        addHistory(node, false)
        return graph.removeVertex(node)
    }

    fun addEdge(e : Edge<Node>) : Edge<Node> {
        return addEdge(e.getSource(), e.getTarget(), e)
    }

    fun addEdge(v : Node, u : Node, edge : Edge<Node>) : Edge<Node> {
        addHistory(v, u, true)
        graph.addEdge(v, u, edge)
        // TODO This is changed once again because of incorrect Supplier implementation
        return edge
    }

    fun addEdge(v : Node, u : Node) : Edge<Node> {
        addHistory(v, u, true)
        val edge = graph.addEdge(v, u)
        edge.setSource(v)
        edge.setTarget(u)
        // TODO This is changed once again because of incorrect Supplier implementation
        return edge
    }

    fun removeEdge(v : Node, u : Node) : Edge<Node> {
        addHistory(v, u, false)
        return graph.removeEdge(v, u)
    }

    fun addHistory(node : Node, add : Boolean) {
        hasChanged = true
        history.push(History(node, add))
    }

    fun addHistory(v : Node, u : Node, add : Boolean) {
        hasChanged = true
        history.push(History(v, u, add))
    }

    inner class History(val node : Node, val add : Boolean) {
        var isNode : Boolean = true
        var otherNode = Node(Coordinate(0f, 0f))
        val neighbors = ArrayList<Node>()

        constructor(v : Node, u : Node, add : Boolean): this(v, add) {
            isNode = false
            otherNode = u
            if (!add) {
                neighbors.addAll(Neighbors.openNeighborhood(graph, v))
            }
        }

        override fun toString(): String {
            val operation : String = if (add) "add" else "del"
            val type : String = if (isNode) "node" else "edge"
            return "$operation $type"
        }
    }
}