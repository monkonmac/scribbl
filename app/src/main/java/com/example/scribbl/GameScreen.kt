package com.example.scribbl

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.scribbl.AppValues.Companion.DEFAULT_BRUSH_WIDTH
import com.example.scribbl.AppValues.Companion.DEFAULT_ERASER_WIDTH

class GameScreen(val dvEventListener: DrawingViewEventListener, val gameScreenActionListener: GameScreenActionListener) : Fragment(), ColorPalette.PaletteListener {
    var dv: DrawingView? = null
    private var mPaint: Paint? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPaint = Paint()
        mPaint?.let {
            it.isAntiAlias = true
            it.isDither = true
            it.color = Color.GREEN
            it.style = Paint.Style.STROKE
            it.strokeJoin = Paint.Join.ROUND
            it.strokeCap = Paint.Cap.ROUND
            it.strokeWidth = DEFAULT_BRUSH_WIDTH
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        dv = DrawingView(activity, mPaint, dvEventListener)
        return dv
    }

    fun passCommand(command: CanvasCommand) {
        dv?.processCommand(command)
    }

    fun setSeekValue(value: Int) {
        dv?.setSeekValue(value)
    }

    override fun colorSelected(color: Int) {
        dv?.setBrushColor(color)
        gameScreenActionListener.colorSelected(color)
    }

    public interface GameScreenActionListener{
        fun colorSelected(color: Int)
    }


}