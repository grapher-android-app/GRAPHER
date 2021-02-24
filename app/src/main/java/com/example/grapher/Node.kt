
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint

import android.view.View


class Node(context: Context?,x: Float, y: Float, w: Int, h: Int) : View(context) {
    var paint : Paint
    var w: Int = w
    var h: Int = h
    var posX = x
    var posY = y

    init {
        paint = Paint()
        paint.setColor(Color.CYAN)
        paint.setStyle(Paint.Style.FILL)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(posX, posY, w.toFloat(), paint)
    }

    fun changeColor(){
        paint.setColor(Color.MAGENTA)
    }
}