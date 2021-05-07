package algorithms

interface ProgressListener {
    fun progress(percent:Float)

    fun progress(k: Int, n: Int)
}