package com.example.grapher

import Node
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import model.DefaultVertex

/** AppCompatActivity replaces Activity in this library */
class GraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        DefaultVertex.resetCounter()
    }
}