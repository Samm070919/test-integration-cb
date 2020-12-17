package com.pagatodoholdings.posandroid.secciones.firma.firmatools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;


/**
 * Created by jesusmario on 27/11/17.
 */

public class SigningView extends View {

    private static final float TOUCH_TOLERANCE = 2f;

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private TextView txtFirma;
    private final Paint mPaint;
    Path path = new Path();
    private Signature signature;

    float previousX;
    float previousY;


    public SigningView(final Context context) {
        super(context);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(3f);
        mPaint.setARGB(0xff,0x33,0x33,0x33);
    }

    public SigningView(final Context context, final TextView txtFirma) {
        super(context);
        setFocusable(true);
        setFocusableInTouchMode(true);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAlpha(100);
        mPaint.setStrokeWidth(5f);
        mPaint.setARGB(0xff,0x33,0x33,0x33);
        this.txtFirma = txtFirma;
        signature = new Signature();

    }

    public void clear() {
        if (this.mCanvas != null) {
            this.mCanvas.drawColor(Color.WHITE);
            invalidate();
            signature =  new Signature();
            txtFirma.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSizeChanged(final int width, final int height, final int oldw, final int oldh) {

        int curW = this.mBitmap != null ? this.mBitmap.getWidth() : 0;
        int curH = this.mBitmap != null ? this.mBitmap.getHeight() : 0;
        if (curW >= width && curH >= height) {
            return;
        }

        if (curW < width){
            curW = width;
        }
        if (curH < height){
            curH = height;
        }


        this.mBitmap = Bitmap.createBitmap(curW, curH, Bitmap.Config.RGB_565);

        this.mCanvas = new Canvas(this.mBitmap);
        this.mCanvas.drawColor(Color.WHITE);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        if (this.mBitmap != null) {
            canvas.drawBitmap(this.mBitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        final float pointX = event.getX();
        final float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                txtFirma.setVisibility(View.VISIBLE);
                touchStart(pointX, pointY);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(pointX, pointY);
                invalidate();
                break;
            default:
                break;
        }
        return true;
    }


    private void touchStart(final float pointX, final float pointY) {
        path.reset();
        path.moveTo(pointX, pointY);
        previousX = pointX;
        previousY = pointY;
        signature.newStroke();

    }

    private void touchMove(final float pointX, final float pointY) {
        final float distanceX = Math.abs(pointX - previousX);
        final float distanceY = Math.abs(pointY - previousY);
        if (distanceX >= TOUCH_TOLERANCE || distanceY >= TOUCH_TOLERANCE) {
            path.quadTo(previousX, previousY, (pointX + previousX)/2, (pointY + previousY)/2);
            mCanvas.drawPath(path, mPaint);
            previousX = pointX;
            previousY = pointY;
        }
    }

    public Signature getSign() {
        return signature;
    }


}
