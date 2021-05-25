package algorithms

import org.jgrapht.graph.SimpleGraph
import java.util.concurrent.Callable
import kotlin.math.min
import kotlin.math.pow

abstract class Algorithm<V, E, Return>(val graph: SimpleGraph<V, E>?): Callable<Return> {

    var cancelFlag : Boolean = false
    var progressListener : ProgressListener? = null

    private val nanoDelay : Long = 20000000L
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


    /**
     * Increases current progress, returns true if current progress has reached
     * its goal.
     *
     * @return true if current >= goal
     */
    fun increaseProgress(): Boolean {
        currentProgress++
        if(currentProgress > progressGoal){
            currentProgress = progressGoal
            return true
        }

        progress(currentProgress, progressGoal)
        return false
    }



    /** Notifies the progress listener of the progress */
    fun progress(percent : Float) {
        if(progressListener != null){
            val now : Long = System.nanoTime()
            if(now-nanoPrev > nanoDelay){
                progressListener!!.progress(percent)
                nanoPrev = now
            }
        }
    }


    /**
     * Notifies the progress listener of the current progress (reached check
     * point k out of n)
     */
    fun progress(k : Int, n : Int) {
        if(progressListener != null){
            val now : Long = System.nanoTime()
            if(now-nanoPrev > nanoDelay){
                progressListener!!.progress(k,n)
                nanoPrev = now
            }
        }
    }
}