package com.example.grapher

import android.content.Context
import android.util.AttributeSet
import android.widget.Switch
import androidx.constraintlayout.widget.ConstraintLayout

// Custom Layout classes need to inherit AttributeSet or else it will fail
class Workspace(context: Context?, attrs : AttributeSet) : ConstraintLayout(context, attrs) {
    private var activity : Context? = context
//    private var graphView = GraphView(activity)

    init {
//        setOnTouchListener(MyOnTouchListener(this))
        this.setWillNotDraw(false)
//        this.addView(graphView)
    }

//    fun setModeSwitch(switch: Switch){
//        graphView.setModeSwitch(switch)
//    }

//
//    // TODO rework this and the listeners into GraphView perhaps?
//    fun createNode(x: Float, y: Float){
//        graphView.addNode(x, y)
//        graphView.invalidate()
//        this.refreshDrawableState()
//
//        // TODO Remove Nodex XML Drawing
//    }

//    class MyGestureListener (private val workspace: Workspace): GestureDetector.SimpleOnGestureListener() {
//        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
//            if (e != null){
//                val x = e.x
//                val y = e.y
//                workspace.createNode(x, y)
//                return true
//            }
//            return false
//        }
//    }
//
//    class MyOnTouchListener(workspace : Workspace) : OnTouchListener{
//        private val gestureListener = MyGestureListener(workspace)
//        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
//            Log.d("ONTOUCH", "onTouched Entered")
//            if (event != null) {
//                if (event.action == MotionEvent.ACTION_DOWN){
//                    Log.d("ONTOUCH", "Action Down")
//                    return gestureListener.onSingleTapConfirmed(event)
//                }
//            }
//            return false
//        }
//    }
}