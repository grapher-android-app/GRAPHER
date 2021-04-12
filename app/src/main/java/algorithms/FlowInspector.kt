package algorithms

import org.jgrapht.graph.SimpleGraph
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class FlowInspector {
    companion object{
        fun <V, E> findFlow(graph: SimpleGraph<V,E>, source: V, target:V){
            val flowEdges : HashSet<Pair<V,V>> = HashSet<Pair<V,V>>()
            var flow: Int = 0

        }

        fun <V,E> flowIncreasingPath(graph: SimpleGraph<V, E>, flowEdges: HashSet<Pair<V,V>>, source: V,
        target: V){
            var prev : Map<V,V> = HashMap<V,V>()
            var next: Queue<V> = LinkedList<V>()

            //initialize search
            next.add(source)


        }
    }
}