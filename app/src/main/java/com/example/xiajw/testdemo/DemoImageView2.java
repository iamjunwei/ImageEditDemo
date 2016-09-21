package com.example.xiajw.testdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xiajw on 2016/9/21.
 */

public class DemoImageView2 extends View {

    private static final int SCALE_POINT_RADIUS = 60;

    private static final int TOUCH_PIC = 0;
    private static final int TOUCH_SCALE_POINT_LEFT_TOP = 1;
    private static final int TOUCH_SCALE_POINT_RIGHT_TOP = 2;
    private static final int TOUCH_SCALE_POINT_LEFT_BOTTOM = 3;
    private static final int TOUCH_SCALE_POINT_RIGHT_BOTTOM = 4;

    private Bitmap bitmap = null;
    private Paint bitmapPaint = new Paint();
    private Paint cropPaint = new Paint();
    private RectF leftTopCircleRect = new RectF();
    private RectF rightTopCircleRect = new RectF();
    private RectF leftBottomRect = new RectF();
    private RectF rightBottomRect = new RectF();
    private RectF cropRect = new RectF();

    private int firstTouchStatus = TOUCH_PIC;

    private float bitmapLeft, bitmapWidth, bitmapTop, bitmapHeight;
    private float cropTop, cropLeft, cropRight, cropBottom;

    private float dx, dy;
    private float mx, my;

    private float rotate = 0f;


    public DemoImageView2(Context context) {
        super(context);
        init();
    }

    public DemoImageView2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemoImageView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBitmap(Bitmap b) {
        if (bitmap != null && bitmap != b) {
            bitmap.recycle();
        }
        bitmap = b;
        bitmapLeft = (getMeasuredWidth() - bitmap.getWidth()) / 2;
        bitmapWidth = bitmap.getWidth();
        bitmapTop = (getMeasuredHeight() - bitmap.getHeight()) / 2;
        bitmapHeight = bitmap.getHeight();
        cropRect.set(bitmapLeft, bitmapTop, bitmapLeft + bitmapWidth, bitmapTop + bitmapHeight);
        cropLeft = bitmapLeft;
        cropRight = bitmapLeft + bitmapWidth;
        cropTop = bitmapTop;
        cropBottom = bitmapTop + bitmapHeight;
        invalidate();
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
        invalidate();
    }

    public void clearBitmap() {
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int w = getMeasuredWidth();
        int h = getMeasuredWidth() * 4 / 3;
        setMeasuredDimension(w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int radius = SCALE_POINT_RADIUS / 2;
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            float scale = (bitmapWidth) / (cropRect.right - cropRect.left);
            float enlarge = getEnlargeScale();
            matrix.postScale(scale, scale, 0, 0);
            matrix.postTranslate(bitmapLeft, bitmapTop);
            matrix.postScale(enlarge, enlarge, getWidth() / 2, getHeight() / 2);
            matrix.postRotate(rotate, getWidth() / 2, getHeight() / 2);
            matrix.postTranslate(mx, my);
            canvas.drawBitmap(bitmap, matrix, bitmapPaint);
        }
        canvas.drawLine(cropLeft, cropTop, cropRight, cropTop, cropPaint);
        canvas.drawLine(cropRight, cropTop, cropRight, cropBottom, cropPaint);
        canvas.drawLine(cropRight, cropBottom, cropLeft, cropBottom, cropPaint);
        canvas.drawLine(cropLeft, cropBottom, cropLeft, cropTop, cropPaint);
        leftTopCircleRect.set(cropLeft - radius, cropTop - radius,
                cropLeft + radius, cropTop + radius);
        rightTopCircleRect.set(cropRight - radius, cropTop - radius,
                cropRight + radius, cropTop + radius);
        leftBottomRect.set(cropLeft - radius, cropBottom - radius,
                cropLeft + radius, cropBottom + radius);
        rightBottomRect.set(cropRight - radius, cropBottom - radius,
                cropRight + radius, cropBottom + radius);
        canvas.drawRect(leftTopCircleRect, cropPaint);
        canvas.drawRect(rightTopCircleRect, cropPaint);
        canvas.drawRect(leftBottomRect, cropPaint);
        canvas.drawRect(rightBottomRect, cropPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                dx = event.getX();
                dy = event.getY();
                mx = 0;
                my = 0;
                firstTouchStatus = getScalePoint(dx, dy);
                break;
            case MotionEvent.ACTION_MOVE:
                if (firstTouchStatus == TOUCH_PIC) {
                    handleMoveEventTouchPic(event);
                } else {
                    handleMoveEventTouchScalePoint(event);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (firstTouchStatus == TOUCH_PIC) {
                    handleUpEventTouchPic(event);
                } else {
                    handleUpEventTouchScalePoint(event);
                }
                break;
        }
        return true;
    }

    private void handleUpEventTouchPic(MotionEvent event) {
        firstTouchStatus = TOUCH_PIC;
        bitmapLeft += mx;
        bitmapTop += my;
        dx = 0;
        dy = 0;
        mx = 0;
        my = 0;
        invalidate();
    }

    private void handleUpEventTouchScalePoint(MotionEvent event) {
        float scale = (cropRect.right - cropRect.left) / (cropRight - cropLeft);
        bitmapWidth *= scale;
        bitmapLeft = cropRect.left -(cropLeft - bitmapLeft) * scale;
        bitmapHeight *= scale;
        bitmapTop = cropRect.top -(cropTop - bitmapTop) * scale;
        switch (firstTouchStatus) {
            case TOUCH_SCALE_POINT_LEFT_TOP:
                break;
            case TOUCH_SCALE_POINT_RIGHT_TOP:
                break;
            case TOUCH_SCALE_POINT_LEFT_BOTTOM:
                break;
            case TOUCH_SCALE_POINT_RIGHT_BOTTOM:
                break;
        }
        cropLeft = cropRect.left;
        cropRight = cropRect.right;
        cropTop = cropRect.top;
        cropBottom = cropRect.bottom;
        firstTouchStatus = TOUCH_PIC;
        dx = 0;
        dy = 0;
        mx = 0;
        my = 0;
        invalidate();
    }

    private void handleMoveEventTouchPic(MotionEvent event) {
        mx += (event.getX() - dx);
        my += (event.getY() - dy);
//        if (bitmapLeft >= cropLeft) bitmapLeft = cropLeft;
//        if (bitmapTop >= cropTop) bitmapTop = cropTop;
//        if (bitmapLeft + bitmapWidth <= cropRight) bitmapLeft = cropRight - bitmapWidth;
//        if (bitmapTop + bitmapHeight <= cropBottom) bitmapTop = cropBottom - bitmapHeight;
        dx = event.getX();
        dy = event.getY();
        invalidate();
    }

    private void handleMoveEventTouchScalePoint(MotionEvent event) {
        float tempX = (event.getX() - dx);
        float tempY = (event.getY() - dy);
        switch (firstTouchStatus) {
            case TOUCH_SCALE_POINT_LEFT_TOP:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = tempX * 3 / 4;
                } else {
                    tempX = tempY * 4 / 3;
                }
                cropLeft += tempX;
                cropTop += tempY;
                break;
            case TOUCH_SCALE_POINT_LEFT_BOTTOM:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = - tempX * 3 / 4;
                } else {
                    tempX = - tempY * 4 / 3;
                }
                cropLeft += tempX;
                cropBottom += tempY;
                break;
            case TOUCH_SCALE_POINT_RIGHT_TOP:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = - tempX * 3 / 4;
                } else {
                    tempX = - tempY * 4 / 3;
                }
                cropRight += tempX;
                cropTop += tempY;
                break;
            case TOUCH_SCALE_POINT_RIGHT_BOTTOM:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = tempX * 3 / 4;
                } else {
                    tempX = tempY * 4 / 3;
                }
                cropRight += tempX;
                cropBottom += tempY;
                break;
        }
        dx = event.getX();
        dy = event.getY();
        invalidate();
    }

    private int getScalePoint(float x, float y) {
        if (leftTopCircleRect.contains(x, y)) return TOUCH_SCALE_POINT_LEFT_TOP;
        if (rightTopCircleRect.contains(x, y)) return TOUCH_SCALE_POINT_RIGHT_TOP;
        if (leftBottomRect.contains(x, y)) return TOUCH_SCALE_POINT_LEFT_BOTTOM;
        if (rightBottomRect.contains(x, y)) return TOUCH_SCALE_POINT_RIGHT_BOTTOM;
        return TOUCH_PIC;
    }

    private float getEnlargeScale() {
        float width = (cropRect.right - cropRect.left);
        float height = (cropRect.bottom - cropRect.top);
        float rotateRad = (float) (Math.abs(rotate) * Math.PI / 180f);
        float scaleRad = (float) Math.atan(height / width);
        float dis = (float) Math.abs(Math.sqrt(width * width / 4 + height * height / 4) * Math.sin(rotateRad + scaleRad));
        float maxDis = dis;
        if (Math.abs(bitmapLeft - (cropRect.left + cropRect.right) / 2) < maxDis) maxDis = Math.abs(bitmapLeft - (cropRect.left + cropRect.right) / 2);
        if (Math.abs(bitmapLeft + bitmapWidth - (cropRect.left + cropRect.right) / 2) < maxDis) maxDis = Math.abs(bitmapLeft + bitmapWidth - (cropRect.left + cropRect.right) / 2);
        if (Math.abs(bitmapTop - (cropRect.top + cropRect.bottom) / 2) < maxDis) maxDis = Math.abs(bitmapTop - (cropRect.top + cropRect.bottom) / 2);
        if (Math.abs(bitmapTop + bitmapHeight - (cropRect.top + cropRect.bottom) / 2) < maxDis) maxDis = Math.abs(bitmapTop + bitmapHeight - (cropRect.top + cropRect.bottom) / 2);
        if (maxDis < dis) {
            return dis / maxDis;
        }
        return 1f;
    }

    private void init() {
        setBackgroundColor(Color.GRAY);
        cropPaint.setColor(Color.WHITE);
        cropPaint.setStrokeWidth(5);
        leftTopCircleRect = new RectF(0, 0, SCALE_POINT_RADIUS * 2, SCALE_POINT_RADIUS * 2);
        rightTopCircleRect = new RectF(leftTopCircleRect);
        leftBottomRect = new RectF(leftTopCircleRect);
        rightBottomRect = new RectF(leftTopCircleRect);
    }

}
