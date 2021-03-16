package com.example.grapher

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import android.widget.Toast.makeText
import androidx.appcompat.app.AppCompatActivity
import model.Node

/** AppCompatActivity replaces Activity in this library */
class GraphActivity : AppCompatActivity() {
    private lateinit var graphView: GraphView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_graph)
        Node.resetCounter()

        //Changes the name of the mode you are in
        graphView = findViewById<GraphView>(R.id.graphView)
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

        val hamburgerButton = findViewById<Button>(R.id.hamburger)
        hamburgerButton?.setOnClickListener{
            showPopUp(hamburgerButton)
        }

    }

    //Maybe put this into a class later, but for now, its a menu :D
    private fun showPopUp(view: View){
        val popMenu = PopupMenu(this, view)
        popMenu.inflate(R.menu.algorithm_menu)

        popMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.test1 -> Toast.makeText(this, "lol", Toast.LENGTH_SHORT).show()
                R.id.test2 -> Toast.makeText(this, "loli", Toast.LENGTH_SHORT).show();
                else -> Toast.makeText(this, "lollol", Toast.LENGTH_SHORT).show();
            }
            true
        }
        popMenu.show()
    }

    override fun onBackPressed() {
        if (!graphView.undo()) {
            super.onBackPressed()
        }
    }
}