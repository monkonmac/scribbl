package com.example.scribbl

interface DrawingViewEventListener {
    fun onEraserSelected(seekValue: Int)
    fun onBrushSelected(seekValue: Int)
    fun resetDrawing()
}