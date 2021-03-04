package com.example.grapher

import Node
import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.Toast

class MyGestureListener (src: GraphActivity): GestureDetector.SimpleOnGestureListener() {
    var src = src
    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        if (e != null){

            val x = e.x
            val y = e.y
            return true
        }
        return false
    }
}