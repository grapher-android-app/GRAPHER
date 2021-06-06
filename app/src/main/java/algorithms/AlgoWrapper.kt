package algorithms

import android.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import com.example.grapher.GraphActivity
import com.example.grapher.GraphView
import com.example.grapher.R
import model.Edge
import model.Node
import java.util.concurrent.FutureTask

abstract class AlgoWrapper<Result>(private val activity: GraphActivity, val algorithm : Algorithm<Node, Edge<Node>, Result?>)
    : FutureTask<Result>(algorithm), IProgressListener{

    private lateinit var dialog: AlertDialog
    private var progressbar: ProgressBar
    init {
        setUpProgressDialog()
        algorithm.progressListener = this
        progressbar = dialog.findViewById<ProgressBar>(R.id.progressBar)
        progressbar.visibility = View.VISIBLE
    }

    fun setTitle(dialogTitle : String) {
        dialog.setTitle(dialogTitle)
    }

    fun setProgressGoal(progressGoal: Int) {
          progressbar.max = progressGoal
    }

    abstract fun resultText(result: Result?) : String

    private fun setUpProgressDialog() {
        val builder = AlertDialog.Builder(activity)

        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.algorithm_progress,null))
        builder.setCancelable(false)

        dialog = builder.create()
        dialog.show()
        Log.d("DIALOG","SHOWING DIALOG")
        val button = dialog.findViewById<Button>(R.id.cancel_button)
        button.setOnClickListener{
            if(!isDone){
                //canceling the algorithm
                algorithm.cancel()
                GraphView.time(false)
                dialog.dismiss()
            }
            //Closes the dialog
           else{
               Log.d("ALGO", "ALGO IS DONE")


                dialog.dismiss()
            }

        }

    }

    override fun progress(percent: Float) {
        Log.d("progress","this is progress $percent")
        progressbar.max = 100
        //progressbar.progress = (percent*100).toInt()
        progressbar.progress = (percent*100).toInt()
    }

    override fun progress(k: Int, n: Int) {
        Log.d("progress","this is progress 2 $k $n")
        progressbar.max = n
        //progressbar.progress = k
        progressbar.progress = k
    }

    override fun run() {
        Log.d("RUN","STARTED RUNNING")
        super.run()
    }

    override fun done() {
        super.done()
        dialog.dismiss()
        resultText(get())
        Log.d("DONE","ALGORITHM DONE")
    }
}