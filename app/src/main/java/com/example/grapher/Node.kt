
import android.content.Context

import android.view.View
import android.view.ViewGroup
import com.example.grapher.R


class Node(context: Context?,x: Float, y: Float, w: Int, h: Int) : View(context) {
    var w: Int = w
    var h: Int = h
    var posX = x
    var posY = y

    init {
        setX(posX-w/2)
        setY(posY-h/2)
        setBackgroundResource(R.drawable.circle)
        var params = ViewGroup.LayoutParams(w, h)
        layoutParams = params
    }
}