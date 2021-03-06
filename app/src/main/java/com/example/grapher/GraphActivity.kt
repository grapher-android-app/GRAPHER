package com.example.grapher

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import model.Node

/** AppCompatActivity replaces Activity in this library */
class GraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        Node.resetCounter()
    }
}