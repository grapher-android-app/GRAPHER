package com.example.grapher

import algorithms.GraphInformation
import algorithms.SpringLayout
import android.graphics.Matrix
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import model.Edge
import model.Node
import org.jgrapht.graph.SimpleGraph
import util.Coordinate
import util.Undo

class GraphViewController(var graphView: GraphView) {

    private var gestureDetector: GestureDetector
    private var gestureListener: MyGestureListener

    private var graph = graphView.graph

    private var layout : SpringLayout? = null

    private var graphWithMemory: Undo = graphView.graphWithMemory

    private var prevPointerCount: Int = 0
    private var prevPointerCoords: Array<Coordinate>? = null

    private var transformMatrix: Matrix = graphView.transformMatrix

    private var matrixScale: Float = 1F
    private var errorMissRadius: Float = 3F

    private var selectedNodes = graphView.selectedNodes

    private var info = ""

    init {
        gestureListener = MyGestureListener()
        gestureDetector = GestureDetector(graphView.context, gestureListener, graphView.handler)
        gestureDetector.setIsLongpressEnabled(true)
        layout = SpringLayout(graph)
        layout!!.iterate(20)
        graphView.setGestureDetector(gestureDetector)
    }

    /**
     * Returns if there is a node on given coordinate
     */
    private fun hasNode(coordinate: Coordinate): Boolean {
        for (node: Node in graph.vertexSet()){
            if (isOnNode(coordinate, node)){
                return true
            }
        }
        return false
    }

    private fun refreshDrawableState(){
        graphView.refreshDrawableState()
    }

    fun addNode(coordinate: Coordinate) {
        val vertex = Node(graphView.node_mode_node_color, coordinate)
        graphWithMemory.addVertex(vertex)
        Log.d("NODE ADDED", graph.toString())
        invalidate()
        refreshDrawableState()
    }

    /**
     * returns Node at given coordinate
     */
    private fun getNodeAtCoordinate(coordinate: Coordinate): Node?{
        for (node: Node in graph.vertexSet()){
            if (isOnNode(coordinate, node)){
                return node
            }
        }
        return null
    }

    private fun markNode(node: Node) {
        selectedNodes.add(node)
        redraw()
    }

    /**
     * marks given node as the selected as the source of the next edge drawn
     */
    private fun selectNode(node: Node){
        graphView.selectedNode = node
        redraw()
        refreshDrawableState()
    }

    /**
     * unselects the selected node
     */
    private fun unselectNode(){
        graphView.selectedNode = null
        redraw()
        refreshDrawableState()
    }

    /**
     * returns true if there exists a selected node
     */
    private fun hasSelectedNode(): Boolean = graphView.selectedNode!=null

    /**
     * Returns true if coordinate is within error radius of given nodes position
     */
    private fun isOnNode(coordinate: Coordinate, node: Node): Boolean{
        if (matrixScale<errorMissRadius){
            return node.getCoordinate().subtract(coordinate).length()<= node.getSize()*errorMissRadius/matrixScale
        }
        else{
            return node.getCoordinate().subtract(coordinate).length()<= node.getSize()
        }
    }

    /**
     * checks if there exists an edge between given node and selected node
     */
    private fun hasEdge(node: Node): Boolean = graph.containsEdge(graphView.selectedNode, node)

    /**
     * adds an edge between given node and selected node
     */
    private fun addEdgeBetween(node: Node){
//        val edge = Edge(selectedNode!!, node)
        graphWithMemory.addEdge(graphView.selectedNode!!, node)
        Log.d("EDGE ADDED", graph.toString())
        unselectNode()
        redraw()
        refreshDrawableState()
    }

    /**
     * removes edge between nodes u and v
     */
    private fun removeEdge(u: Node, v: Node){
        graphWithMemory.removeEdge(u, v)
        unselectNode()
        redraw()
    }

    /**
     * removes the given node, including all edges
     */
    private fun removeNode(u: Node): Boolean = graphWithMemory.removeVertex(u)

    /**
     * moves the selected node to given coordinate
     */
    private fun moveNode(coordinate: Coordinate){
        graphView.selectedNode!!.setCoordinate(coordinate)
    }

    private fun redraw(){
        graphView.redraw()
    }

    private fun invalidate(){
        graphView.invalidate()
    }

    fun undo() : Boolean {
        val ret = graphWithMemory.undo()
        redraw()
        return ret
    }

    fun longShake(n: Int) {
        if (layout == null) {
            layout = SpringLayout(graph)
        }
        layout!!.iterate(n)
        invalidate()
    }

    fun shake() {
        if (layout == null) {
            layout = SpringLayout(graph)
        }
        longShake(20)
        invalidate()
    }

    /**
     * translates the input coordinate to a transformMatrix coordinate
     */
    private fun translateCoordinate(screenCoordinate: Coordinate): Coordinate {
        val screenPoint = floatArrayOf(screenCoordinate.getX(), screenCoordinate.getY())
        val invertedTransformMatrix = Matrix()
        transformMatrix.invert(invertedTransformMatrix)
        invertedTransformMatrix.mapPoints(screenPoint)
        return Coordinate(screenPoint[0], screenPoint[1])
    }

    fun getGraph() : SimpleGraph<Node, Edge<Node>> {
        return graph
    }

    fun graphInfo() : String {
        info = GraphInformation.graphInfo(graph)
        return info
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (e != null){
                prevPointerCoords = null
                val coordinate = translateCoordinate(Coordinate(e.x, e.y))
                if (graphView.nodeMode){
                    if (!hasNode(coordinate)){
                        Log.d("OnSingleTapConfirmed", "Added Node")
                        addNode(coordinate)
                    }
                    else {
                        markNode(getNodeAtCoordinate(coordinate)!!)
                        Log.d("OnSingleTapConfirmed", "Selected Node")
                    }
                }
                else {
                    if (hasNode(coordinate)){
                        Log.d("OnSingleTapConfirmed", "Was Node")
                        val node = getNodeAtCoordinate(coordinate)!!
                        if (hasSelectedNode()){
                            if (node==graphView.selectedNode){ // Unselect node
                                Log.d("OnSingleTapConfirmed", "Unselected Node")
                                unselectNode()
                            }
                            else{
                                if (hasEdge(node)){ //Already edge between two selected nodes
                                    removeEdge(graphView.selectedNode!!, node)
                                    Log.d("OnSingleTapConfirmed", "Removed Edge")
                                }
                                else { //No edge
                                    addEdgeBetween(node)
                                    Log.d("OnSingleTapConfirmed", "Added Edge")
                                }
                            }
                        }
                        else {
                            Log.d("OnSingleTapConfirmed", "Selected Node")
                            selectNode(node)
                        }
                    }
                    else{
                        Log.d("OnSingleTapConfirmed", "Wasn't Node")
                    }
                }
                return true
            }
            return false
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            Log.d("onDoubleTap", "onSingleTapConfirmed")
            if (e!=null) {
                if (graphView.nodeMode) {
                    val coordinate = translateCoordinate(Coordinate(e.x, e.y))
                    if (hasNode(coordinate)){
                        val node = getNodeAtCoordinate(coordinate)
                        removeNode(node!!)
                        redraw()
                    }
                }
            }
            return super.onDoubleTap(e)
        }

        override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent?,
                distanceX: Float,
                distanceY: Float
        ): Boolean {
            if (e1!=null && e2!=null){
                if (e2.pointerCount==1){
                    prevPointerCoords = null
                    if (graphView.nodeMode) {
                        val coordinate = translateCoordinate(Coordinate(e2.x, e2.y))
                        if (graphView.isScrolling) {
                            moveNode(coordinate)
                        } else {
                            if (hasNode(coordinate)) {
                                val node = getNodeAtCoordinate(coordinate)
                                selectNode(node!!)
                                moveNode(coordinate)
                                graphView.isScrolling = true
                            }
                        }
                    }
                }
                else if (e2.pointerCount==2){ // 2 fingers
                    if (prevPointerCoords==null || prevPointerCount != 2) {
                        prevPointerCoords = arrayOf(Coordinate(e2.getX(0), e2.getY(0)), Coordinate(e2.getX(1), e2.getY(1)))
                    } else {
                        val newCoords = arrayOf(
                                Coordinate(e2.getX(0), e2.getY(0)),
                                Coordinate(e2.getX(1), e2.getY(1))
                        )
                        val vectorPrevious = prevPointerCoords!![1].subtract(prevPointerCoords!![0])
                        val vectorNew = newCoords[1].subtract(newCoords[0])
                        val diffAngle = vectorNew.angle() - vectorPrevious.angle()
                        val scale = vectorNew.length() / vectorPrevious.length()

                        // the transformations
                        transformMatrix.postTranslate(
                                -prevPointerCoords!![0].getX(),
                                -prevPointerCoords!![0].getY()
                        )
                        transformMatrix.postRotate(diffAngle)
                        transformMatrix.postScale(scale, scale)
                        matrixScale *= scale
                        transformMatrix.postTranslate(newCoords[0].getX(), newCoords[0].getY())
                        prevPointerCoords = newCoords
                    }
                } else { // 3 or more
                    prevPointerCoords = null
                    prevPointerCount=e2.pointerCount
                    return false
                }
                prevPointerCount=e2.pointerCount
                invalidate()
                return true
            }
            prevPointerCoords=null
            return false
        }

        override fun onLongPress(e: MotionEvent?) {
            Log.d("GESTURE LISTENER", "onLongPress")
        }

        /**
         * This always returns true, because if it didn't, then long press would always be triggered
         */
        override fun onDown(e: MotionEvent?): Boolean{
            prevPointerCount=-1
            return true
        }
    }
}