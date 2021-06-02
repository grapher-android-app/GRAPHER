package com.example.grapher

import algorithms.AlgoWrapper
import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import model.Node
import org.jgrapht.Graph
import util.GraphExporter
import java.lang.Exception
import java.lang.Math.sqrt
import java.util.*

/** AppCompatActivity replaces Activity in this library */
class GraphActivity : AppCompatActivity() {
    private lateinit var graphView: GraphView
    private lateinit var graphViewController: GraphViewController

    //shake function values for shaking phone
    var sensorManager: SensorManager? = null
    var acceleration = 0f
    var currentAcceleration = 0f
    var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //for shake function with phone
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!.registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)
        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH



        setContentView(R.layout.activity_graph)
        Node.resetCounter()

        //Changes the name of the mode you are in
        graphView = findViewById<GraphView>(R.id.graphView)
        graphViewController= GraphViewController(graphView)
        val switchMode = findViewById<Switch>(R.id.mode_switch)
        switchMode?.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                findViewById<TextView>(R.id.mode_text).text = "Edge"
                graphView.changeMode()
            }
            else{
                findViewById<TextView>(R.id.mode_text).text = "Node"
                graphView.changeMode()
            }
        }
        //Normalizes graph
        val shakeButton = findViewById<Button>(R.id.shake_button)
        shakeButton?.setOnClickListener {
            graphViewController.shake()
        }

        //shows menu when clicked
        val hamburgerButton = findViewById<Button>(R.id.hamburger)
        hamburgerButton?.setOnClickListener{
            showPopUp(hamburgerButton)
        }
    }

    fun graphInfo(){
        val builder = AlertDialog.Builder(this)

        val inflater = layoutInflater
        builder.setView(inflater.inflate(R.layout.graphinfo,null))
        builder.setCancelable(false)

        var dialog = builder.create()
        dialog.show()
        dialog.findViewById<TextView>(R.id.graphInfo).text = graphView.graphInfo()
        dialog.findViewById<Button>(R.id.ok_button).setOnClickListener { dialog.hide() }
    }

    //Based on  https://www.tutorialspoint.com/how-to-detect-shake-events-in-kotlin (read: 2/6/21)
    val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration
            currentAcceleration = kotlin.math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta
            if (acceleration > 9.75) {
                graphViewController.shake()
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }
    override fun onResume() {

        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }
    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }



    /**
        Inflates the menu with algorithms and listens for the user to click items in it.
        When user clicks, the algorithm will run.
        @param view
     */
    private fun showPopUp(view: View){
        val popMenu = PopupMenu(this, view)
        popMenu.inflate(R.menu.algorithm_menu)


        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.show_center -> {
                    var conn : Boolean = graphView.showCenterNode()
                    if (!conn) shortToast("No center vertex in disconnected graph")
                }
                R.id.compute_cycle_4 -> {
                    graphView.showAllCycle4()
                }
                R.id.min_dominating_set -> {
                    graphView.exactDominatingSet(this)
                }
                R.id.power -> {
                    graphView.constructPower()
                }
                R.id.hamiltonian_path -> {
                    graphView.showHamiltonianPath(this)
                }
                R.id.hamiltonian_cycle -> {
                    graphView.showHamiltonianCycle(this)
                }
                R.id.optimal_coloring -> {
                    graphView.showOptimalColoring(this)
                }
                R.id.AllBridges -> {
                    if (!graphView.showAllBridges()){
                        shortToast("No bridges")
                    }
                }
                R.id.allCuts -> {
                    graphView.showAllCuts()
                }
                R.id.flow -> {
                    val flow = graphView.showFlow()
                    when {
                        flow < 0 -> shortToast("Please select two nodes (tap in Node mode)")
                        flow == 0 -> shortToast("Not connected")
                        else -> shortToast("Max flow $flow")
                    }
                }
                R.id.power -> {
                    graphView.constructPower()
                    shortToast("Power graph has been constructed")
                }
                R.id.bipartition -> {
                    val bip = graphView.isBipartite()
                    when{
                    bip -> shortToast("Graph is bipartite ")
                    else -> shortToast("Graph is not bipartite")
                    }
                }
                R.id.test_eulerian -> {
                    val eulerian = graphView.isEulerian()
                    when{
                        eulerian -> shortToast("Graph is Eulerian ")
                        else -> shortToast("Graph is not Eulerian")
                    }
                }

                R.id.diameter -> {
                    val diam: Int? = graphView.diameterInsp()
                    if (diam!! > 0){shortToast("The diameter is $diam")}
                    else {shortToast("Graph has no diameter")}
                }
                R.id.show_info -> {
                    graphInfo()
                }

                R.id.claws ->{
                   val claw = graphView.ClawInsp()
                    if(claw)  shortToast("Graph has a claw")
                    else{
                        shortToast("Graph is claw free")
                    }
                }

                R.id.clearGraph -> {
                    finish()
                    startActivity(intent)
                    shortToast("Graph was cleared")
                }

                R.id.share_metapost -> {
                    shareMetapost()
                }

                R.id.metapost_to_clipboard -> {
                    if(copyMetapostToClipboard()) {
                        shortToast("Copied info on ${graphViewController}")
                    }
                    else {
                        shortToast("An error occurd copying to clipboard!")
                    }
                }

                else -> null
            }
            true
        }
        popMenu.show()
    }

    override fun onBackPressed() {
        if (!graphViewController.undo()) {
            super.onBackPressed()
        }
    }

    /**
     * Generates a short toast
     * @param s string to display
     */
    private fun shortToast(toast : String) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show()
    }

    private fun copyMetapostToClipboard() : Boolean {
        val text = GraphExporter.getMetapost(graphViewController.getGraph())
        return try {
            val clipboard : ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Graph Information", text)
            clipboard.setPrimaryClip(clip)
            true
        }
        catch (e : Exception) {
            println("Error while copying metapost to clipboard: $e")
            e.printStackTrace()
            false
        }
    }

    private fun shareMetapost() {
        var shareBody = GraphExporter.getMetapost(graphViewController.getGraph())
        shareBody += "\n\n% Sent to you by Grapher"

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        //TODO add graphInfo method
        //sharingIntent.putExtra(Intent.EXTRA_SUBJECT, graphViewController.graphInfo())
        //sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(Intent.createChooser(sharingIntent, "Share graph with"))
    }

}