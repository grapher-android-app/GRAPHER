package algorithms

import model.Edge
import model.Node
import org.jgrapht.graph.SimpleGraph
import util.Neighbors

class PowerGraph {


    companion object{



        /**
         *@param graph
         * @return new graph resuing vertex object
         *
         */
        fun constructPowerGraph(graph: SimpleGraph<Node, Edge<Node>>): SimpleGraph<Node,Edge<Node>>{
            return constructPowerGraph(graph,2)
        }


        /**
        * @param graph any
         * @param n any Int > 0?
         *
         * @return new graph reusing vertex object
         */
        fun constructPowerGraph(graph: SimpleGraph<Node,Edge<Node>>, n: Int): SimpleGraph<Node, Edge<Node>> {
            // Takes a graph and creates the power graph of the given graph
            var power = SimpleGraph<Node, Edge<Node>>(null, graph.edgeSupplier, false)
            for(v: Node in graph.vertexSet()){
                power.addVertex(v)
            }


            for(v: Node in graph.vertexSet()){
                val neighbors : Set<Node> = Neighbors.openNNeighborhood(graph, v, n)
                for(u: Node in neighbors) if(!power.containsEdge(v,u)) {
                    //TODO gonna crash because supplier
                    val edge = Edge<Node>()
                    edge.setSource(v)
                    edge.setTarget(u)
                    power.addEdge(v,u, edge)
                }
            }
            return power
        }




    }
}