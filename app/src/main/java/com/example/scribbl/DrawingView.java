package com.example.scribbl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.MotionEvent;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DrawingView extends View {

    public int width;
    public  int height;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private ArrayList<Path> pathList;
    private Paint mBitmapPaint;
    Context context;
    private Paint circlePaint;
    private Path circlePath;
    private Paint mPaint;
    private Paint mDrawPaint;
    private Paint mEraser;
    private DrawingViewEventListener mDvEventListener;
    private boolean enableDrawing = true;

    private boolean ERASER_ACTIVE = false;

    public DrawingView(Context c, Paint mPaint, DrawingViewEventListener dvEventListener) {
        super(c);
        context=c;
        this.mDrawPaint = mPaint;
        this.mPaint = mDrawPaint;
        this.mDvEventListener = dvEventListener;
        this.mEraser = new Paint();
        initEraser();
        pathList = new ArrayList<>();
        mPath = new Path();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        circlePaint = new Paint();
        circlePath = new Path();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeJoin(Paint.Join.MITER);
        circlePaint.setStrokeWidth(4f);
        setBackgroundColor(Color.parseColor("#ffffff"));
    }

    private void initEraser() {
        mEraser.setAlpha(0);
        mEraser.setColor(Color.TRANSPARENT);
        mEraser.setStrokeWidth(AppValues.getDEFAULT_ERASER_WIDTH());
        mEraser.setStyle(Paint.Style.STROKE);
        mEraser.setMaskFilter(null);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mEraser.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap( mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
        canvas.drawPath( circlePath,  circlePaint);
    }

    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;

    private void touch_start(float x, float y) {
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
            mX = x;
            mY = y;

            circlePath.reset();
            circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
        }
    }

    private void touch_up() {
        mPath.lineTo(mX, mY);
        circlePath.reset();
        // commit the path to our offscreen
        mCanvas.drawPath(mPath,  mPaint);
        // kill this so we don't double draw
        pathList.add(mPath);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if(enableDrawing) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
        }
        return true;
    }

    public void processCommand(@NotNull CanvasCommand command) {
        switch (command){
            case BRUSH:
                startBrush();
                break;
            case PALETTE:
                popPallete();
                break;
            case RESET:
                resetCanvas();
                break;
            case ERASER:
                startEraser();
                break;
            case ENABLE_DRAWING:
                drawingStatusChanged(true);
                break;
            case DISABLE_DRAWING:
                drawingStatusChanged(false);
                break;
            default:
                break;
        }
    }

    private void drawingStatusChanged(boolean isDrawingEnabled) {
        enableDrawing = isDrawingEnabled;
        resetCanvas();
    }

    private void startBrush() {
        ERASER_ACTIVE = false;
        mPaint = mDrawPaint;
        mDvEventListener.onBrushSelected((int) mPaint.getStrokeWidth());
    }

    public void startEraser() {
        ERASER_ACTIVE = true;
        mPaint = mEraser;
        mDvEventListener.onEraserSelected((int) mPaint.getStrokeWidth());
    }

    public void resetCanvas(){
        mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        invalidate();
        mEraser.setStrokeWidth(AppValues.getDEFAULT_ERASER_WIDTH());
        mDrawPaint.setStrokeWidth(AppValues.getDEFAULT_BRUSH_WIDTH());
        startBrush();
    }

    private void popPallete() {

    }

    public void setSeekValue(int value) {
        if(ERASER_ACTIVE){
            mEraser.setStrokeWidth(value);
        }else{
            mDrawPaint.setStrokeWidth(value);
        }
    }

    public void setBrushColor(int color) {
        mDrawPaint.setColor(color);
        startBrush();
    }

    //Future feature
//    public void undoLastMove() {
//        int pathSize = pathList.size();
//        if(pathSize > 0){
//            pathList.get(pathSize-1).reset();
//            pathList.remove(pathSize-1);
//            invalidate();
//        }
//    }

}
