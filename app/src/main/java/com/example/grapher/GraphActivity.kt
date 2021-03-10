package com.example.grapher

import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import model.Node

/** AppCompatActivity replaces Activity in this library */
class GraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_graph)
        Node.resetCounter()

        //Changes the name of the mode you are in
        val switchMode = findViewById<Switch>(R.id.mode_switch)
        switchMode?.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                findViewById<TextView>(R.id.mode_text).text = "Edge"
            }
            else{
                findViewById<TextView>(R.id.mode_text).text = "Node"
            }

        }

    }


}