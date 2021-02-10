package com.example.grapher

import model.DefaultVertex;

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

/** AppCompatActivity replaces Activity in this library */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DefaultVertex.resetCounter()
    }
}