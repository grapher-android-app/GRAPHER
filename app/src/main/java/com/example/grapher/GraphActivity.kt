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
    lateinit var grid_view: RelativeLayout
    lateinit var testNode: Node

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)
        grid_view = findViewById(R.id.workspace)
        grid_view.setOnTouchListener(MyOnTouchListener(this))
        DefaultVertex.resetCounter()


        /*var addNodeButton = findViewById<Button>(R.id.addNodeButton)
        addNodeButton.setOnClickListener{
            val text = "Hello toast!"
            val duration = Toast.LENGTH_SHORT

            val toast = Toast.makeText(this, text, duration)
            toast.show()
            var button = Button(this)
            grid_view.addView(button,0)
            grid_view.refreshDrawableState()
        }*/
    }

    fun createNode(x: Float, y: Float){
        var node = Node(this,x,y,50,50)
        grid_view.addView(node,0)
    }

    class MyOnTouchListener(src: GraphActivity) : View.OnTouchListener{
        var src = src
        val gestureListener = MyGestureListener(src)
        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN){
                    return gestureListener.onSingleTapConfirmed(event)
                }
            }
            return false
        }
    }
}