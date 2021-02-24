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
            val text = "Hello toast!"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(src, text, duration)
            toast.show()
            var x = e.x
            var y = e.y
            src.createNode(x,y)
            return true
        }
        return false
    }
}