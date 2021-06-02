package algorithms

import org.jgrapht.graph.SimpleGraph
import org.jgrapht.alg.connectivity.ConnectivityInspector
import util.Neighbors

class CutAndBridgeInspector {


    companion object{

        /**
         * Returns a vertex v of type V which is a cut vertex in given graph or null
         * if none exists.
         *
         * @param graph
         *            Graph to find a cut vertex
         * @return a cut vertex or null if none exists.
         */

        fun <V,E> findCutVertex(graph: SimpleGraph<V,E>): V?{
            @SuppressWarnings("Unchecked")
            var gc: SimpleGraph<V, E> = graph.clone() as SimpleGraph<V, E>
            val size = ConnectivityInspector<V, E>(graph).connectedSets().size
            for(v: V in graph.vertexSet()) {
                if (graph.degreeOf(v) < 2) continue
                gc.removeVertex(v)
                val nSize: Int = ConnectivityInspector<V, E>(gc).connectedSets().size
                if (nSize > size) return v
                gc.addVertex(v)
                for (u: V in Neighbors.openNeighborhood(graph, v)) {
                    gc.addEdge(u, v)
                }
            }
            return null
        }

        /**
         * Finds the set of all cut vertices in a graph. This returns an empty set
         * if and only if findCutVertex returns null if and only if there are no cut
         * vertices in graph if and only if the graph is biconnected.
         *
         * @param graph
         *            input graph to locate cut vertices
         * @return a possibly empty set of cut vertices
         */

        fun <V, E> findAllCutVertices(graph: SimpleGraph<V,E>): HashSet<V>{
            var cuts = HashSet<V>()
            @SuppressWarnings("Unchecked")
            var gc: SimpleGraph<V, E> = graph.clone() as SimpleGraph<V, E>
            val size = ConnectivityInspector<V, E>(graph).connectedSets().size

            for(v: V in graph.vertexSet()){
                if(graph.degreeOf(v)< 2) continue
                gc.removeVertex(v)
                val nSize: Int = ConnectivityInspector<V, E>(gc).connectedSets().size
                if(nSize > size) cuts.add(v)
                gc.addVertex(v)
                for(u: V in Neighbors.openNeighborhood(graph,v)){
                    gc.addEdge(u, v)
                }
            }
            return cuts
        }

        /**
         * Finds and return a bridge (isthmus) if and only if the graph has a
         * bridge, returns null otherwise.
         *
         * @param graph
         *            input graph to find bridge
         * @return a bridge or null if none exists
         */
        fun <V,E> findBridge(graph: SimpleGraph<V,E>): E?{
            @SuppressWarnings("unchecked")
            var gc: SimpleGraph<V, E> = graph.clone() as SimpleGraph<V, E>
            val size = ConnectivityInspector<V, E>(graph).connectedSets().size

            for(e: E in graph.edgeSet()){
                gc.removeEdge(e)
                val nSize: Int = ConnectivityInspector<V, E>(gc).connectedSets().size
                if(nSize > size) return e
                val source: V = graph.getEdgeSource(e)
                val target: V = graph.getEdgeTarget(e)
                gc.addEdge(source,target)
            }
            return null
        }




        /**
         * Finds and returns the set of bridges (isthmuses). Returns the empty set
         * if and only if the graph has no bridge if and only if findBridge returns
         * null.
         *
         * @param graph
         *            input graph to find bridge
         * @return a (possibly empty) set of bridges
         */
        fun <V, E> findAllBridges(graph: SimpleGraph<V, E>): HashSet<E> {
            var bridges = HashSet<E>()
            @SuppressWarnings("unchecked")
            var gc: SimpleGraph<V,E> = graph.clone() as SimpleGraph<V, E> //idk om det er rett å gjøre sånn
            val size: Int = ConnectivityInspector<V,E>(graph).connectedSets().size

            for(e: E in graph.edgeSet()){
                gc.removeEdge(e)
                val nSize = ConnectivityInspector<V,E>(gc).connectedSets().size
                if(nSize > size){
                    bridges.add(e)
                }
                val source: V = graph.getEdgeSource(e)
                val target: V = graph.getEdgeTarget(e)
                gc.addEdge(source,target)
            }
            return bridges


        }

    }
}