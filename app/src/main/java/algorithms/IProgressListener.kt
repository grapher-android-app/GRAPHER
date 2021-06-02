package algorithms

interface IProgressListener {
    fun progress(percent:Float)

    fun progress(k: Int, n: Int)
}