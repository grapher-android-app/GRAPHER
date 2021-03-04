package com.example.grapher

import Node
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import org.jgrapht.Graph

// Custom Layout classes need to inherit AttributeSet or else it will fail
class Workspace(context: Context?, attrs : AttributeSet) : ConstraintLayout(context, attrs) {
    var activity : Context? = context


    init {
        Log.d("YO", "intialized")
        setOnTouchListener(MyOnTouchListener(this))
        this.setWillNotDraw(false)
    }

    fun createNode(x: Float, y: Float){
        var node = Node(activity,x,y,50,50)
        this.addView(node,0)
        this.refreshDrawableState()
    }

    class MyGestureListener (workspace: Workspace): GestureDetector.SimpleOnGestureListener() {
        private val workspace = workspace
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            Log.d("HELP", "Meow")
            if (e != null){
                var x = e.x
                var y = e.y
                workspace.createNode(x, y)
                return true
            }
            return false
        }
    }

    class MyOnTouchListener(workspace : Workspace) : OnTouchListener{
        private val gestureListener = MyGestureListener(workspace)
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            Log.d("ONTOUCH", "onTouched Entered")
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN){
                    Log.d("ONTOUCH", "Action Down")
                    return gestureListener.onSingleTapConfirmed(event)
                }
            }
            return false
        }
    }
}