package algorithms

import org.jgrapht.graph.SimpleGraph
import kotlin.math.min
import kotlin.math.pow

abstract class Algorithm<V, E, Return>(val graph: SimpleGraph<V, E>?) {

    var cancelFlag : Boolean = false
    // val progressListener : ProgressListener

    private val nanoDelay : Long = 0L
    private var nanoPrev = System.nanoTime() - nanoDelay

    private var progressGoal : Int
    private var currentProgress = 0

    init {
        progressGoal = 2.0.pow(graphSize()).toInt()
    }

    fun graphSize() : Int {
        if (graph != null) {
            return graph.vertexSet().size
        }
        return 0
    }

    fun graphEdgeSize() : Int {
        if (graph != null) {
            return graph.edgeSet().size
        }
        return 0
    }

    fun cancel() {
        println("We have been cancelled.")
        cancelFlag = true
    }

    fun setProgressGoal(progressGoal : Int) {
        this.progressGoal = progressGoal
        currentProgress = min(currentProgress, progressGoal)
    }

    fun setCurrentProgress(currentProgress : Int) : Boolean {
        if (currentProgress > progressGoal) {
            return false
        }
        this.currentProgress = currentProgress
        return true
    }

    /** Notifies the progress listener of the progress */
    fun progess(percent : Float) {
        //TODO implement
    }

    fun progress(k : Int, n : Int) {
        //TODO implement
    }

    abstract fun execute() : Return
}