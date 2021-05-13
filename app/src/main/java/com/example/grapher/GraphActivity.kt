package com.example.grapher

import algorithms.AlgoWrapper
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import model.Node
import org.jgrapht.Graph

/** AppCompatActivity replaces Activity in this library */
class GraphActivity : AppCompatActivity() {
    private lateinit var graphView: GraphView
    private lateinit var graphViewController: GraphViewController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        val shakeButton = findViewById<Button>(R.id.shake_button)
        shakeButton?.setOnClickListener {
            graphViewController.shake()
        }

        val hamburgerButton = findViewById<Button>(R.id.hamburger)
        hamburgerButton?.setOnClickListener{
            showPopUp(hamburgerButton)
        }

    }

    //Maybe put this into a class later, but for now, its a menu :D
    private fun showPopUp(view: View){
        val popMenu = PopupMenu(this, view)
//        var progressDialog = ProgressDialog(this)

        popMenu.inflate(R.menu.algorithm_menu)


        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.test1 -> Toast.makeText(this, "lol", Toast.LENGTH_SHORT).show()
                R.id.show_center -> {
                    var conn : Boolean = graphView.showCenterNode()
                    // TODO create easy toast feedback

//                    progressDialog.startProgressDialog()


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
                else -> Toast.makeText(this, "lollol", Toast.LENGTH_SHORT).show()
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

}