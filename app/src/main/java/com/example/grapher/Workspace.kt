package com.example.grapher

import Node
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout

// Custom Layout classes need to inherit AttributeSet or else it will fail
class Workspace(context: Context?, attrs : AttributeSet) : ConstraintLayout(context, attrs) {
    private var activity : Context? = context
    private var graphView = GraphView(activity)

    init {
        setOnTouchListener(MyOnTouchListener(this))
        this.setWillNotDraw(false)
        this.addView(graphView)
    }

    fun createNode(x: Float, y: Float){
        val node = Node(activity,x,y,50,50)
        graphView.addVertex(x, y)
        graphView.invalidate()
        this.addView(node,0)
        this.refreshDrawableState()
    }

    class MyGestureListener (private val workspace: Workspace): GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (e != null){
                val x = e.x
                val y = e.y
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