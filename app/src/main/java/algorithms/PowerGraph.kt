package algorithms

import org.jgrapht.graph.SimpleGraph
import util.Neighbors

class PowerGraph {


    companion object{



        /**
         *@param graph
         * @return new graph resuing vertex object
         *
         */
        fun <V, E> constructPowerGraph(graph: SimpleGraph<V, E>): SimpleGraph<V,E>{
            return constructPowerGraph(graph,2)
        }


        /**
        * @param graph any
         * @param n any Int > 0?
         *
         * @return new graph reusing vertex object
         */
        fun <V, E> constructPowerGraph(graph: SimpleGraph<V,E>, n: Int): SimpleGraph<V, E> {
            // Takes a graph and creates the power graph of the given graph
            var power = SimpleGraph<V,E>(graph.edgeSupplier)
            for(v: V in graph.vertexSet()){
                power.addVertex(v)
            }


            for(v: V in graph.vertexSet()){
                val neigs : Set<V> = Neighbors.openNNeighborhood(graph, v, n)
                for(u: V in neigs) if(!power.containsEdge(v,u)) {
                    power.addEdge(v,u)
                }
            }
            return power
        }




    }
}