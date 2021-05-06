package algorithms

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.AsyncTask
import com.example.grapher.GraphActivity
import com.example.grapher.GraphView
import model.Edge
import model.Node

abstract class AlgoWrapper<Result>(val activity: GraphActivity, val algorithm : Algorithm<Node, Edge<Node>, Result?>)
    : AsyncTask<Void, Integer, Result>() {

    var pDialog : ProgressDialog = ProgressDialog(activity)

    init {
        setUpProgressDialog()
    }

    constructor(activity: GraphActivity, algorithm: Algorithm<Node, Edge<Node>, Result?>, progressTitle: String)
        : this(activity, algorithm) {
        pDialog.setTitle(progressTitle)

    }

    fun setTitle(dialogTitle : String) {
        pDialog.setTitle(dialogTitle)
    }

    override fun onCancelled() {
        pDialog.cancel()
        GraphView.time(false)
    }

    override fun doInBackground(vararg params: Void?): Result? {
        GraphView.time(true)
        return algorithm.execute()
    }

    override fun onPreExecute() {
        val myOnCancelListener = object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface?) {
                cancel(true)
                algorithm.cancel()
                GraphView.time(false)
            }
        }
    }

    abstract fun resultText(result: Result?) : String

    override fun onPostExecute(result: Result) {
        pDialog.dismiss()
        val resDialog = AlertDialog.Builder(activity)

        resDialog.setMessage(resultText(result))
        resDialog.setTitle("Result")
        resDialog.setPositiveButton("OK", null)
        resDialog.create().show()

        GraphView.time(false)
    }

    override fun onProgressUpdate(vararg values: Integer?) {
        for (progress : Integer? in values) {
            if (progress != null) {
                pDialog.progress = progress.toInt()
            }
        }
    }

    private fun setUpProgressDialog() {
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
        pDialog.isIndeterminate = false
        pDialog.setCancelable(false)
        pDialog.setTitle("Computing...")

        var myOnClickListener = DialogInterface.OnClickListener { dialog, which -> pDialog.cancel() }
        pDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel", myOnClickListener)
    }
    //execute in Algorithms
}