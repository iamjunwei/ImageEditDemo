package com.example.xiajw.testdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xiajw on 2016/9/21.
 */

public class DemoImageViewStable3 extends View {

    private static final int SCALE_POINT_RADIUS = 60;

    private static final int TOUCH_PIC = 0;
    private static final int TOUCH_SCALE_POINT_LEFT_TOP = 1;
    private static final int TOUCH_SCALE_POINT_RIGHT_TOP = 2;
    private static final int TOUCH_SCALE_POINT_LEFT_BOTTOM = 3;
    private static final int TOUCH_SCALE_POINT_RIGHT_BOTTOM = 4;

    private Bitmap bitmap = null, originalBit;
    private Paint bitmapPaint = new Paint();
    private Paint cropPaint = new Paint();
    private RectF leftTopCircleRect = new RectF();
    private RectF rightTopCircleRect = new RectF();
    private RectF leftBottomRect = new RectF();
    private RectF rightBottomRect = new RectF();
    private RectF cropRect = new RectF();
    private int originWidth, originHeight;

    private int firstTouchStatus = TOUCH_PIC;

    private float bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y;
    private float cropTop, cropLeft, cropRight, cropBottom;

    private float dx, dy;
    private float sx, sy;

    private float rotate = 0f;

    public DemoImageViewStable3(Context context) {
        super(context);
        init();
    }

    public DemoImageViewStable3(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemoImageViewStable3(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setBitmap(Bitmap b) {
        if (bitmap != null && bitmap != b) {
            bitmap.recycle();
        }
        bitmap = b;
        originalBit = b;
        originWidth = b.getWidth();
        originHeight = b.getHeight();
        int bitmapLeft = (getMeasuredWidth() - bitmap.getWidth()) / 2;
        int bitmapWidth = bitmap.getWidth();
        int bitmapTop = (getMeasuredHeight() - bitmap.getHeight()) / 2;
        int bitmapHeight = bitmap.getHeight();
        cropRect.set(bitmapLeft, bitmapTop, bitmapLeft + bitmapWidth, bitmapTop + bitmapHeight);
        cropLeft = bitmapLeft;
        cropRight = bitmapLeft + bitmapWidth;
        cropTop = bitmapTop;
        cropBottom = bitmapTop + bitmapHeight;
        bitmapP1x = bitmapLeft;
        bitmapP1y = bitmapTop;
        bitmapP2x = bitmapLeft + bitmapWidth;
        bitmapP2y = bitmapTop;
        bitmapP3x = bitmapLeft;
        bitmapP3y = bitmapTop + bitmapHeight;
        bitmapP4x = bitmapLeft + bitmapWidth;
        bitmapP4y = bitmapTop + bitmapHeight;
        invalidate();
    }

    public void setRotate(float rotate) {
        Matrix m = new Matrix();
        m.postRotate(rotate);
        Bitmap newBitmap = Bitmap.createBitmap(originalBit, 0, 0, originalBit.getWidth(), originalBit.getHeight(), m, false);
        if (newBitmap != null && newBitmap != bitmap && bitmap != originalBit) {
            bitmap.recycle();
        }
        bitmap = newBitmap;
        getRotatePoint(rotate - this.rotate);
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
            float scale = (float) (Math.sqrt((bitmapP2x - bitmapP1x) * (bitmapP2x - bitmapP1x) + (bitmapP2y - bitmapP1y) * (bitmapP2y - bitmapP1y)) / originWidth);
            matrix.postScale(scale, scale, 0, 0);
            matrix.postTranslate(getBitmapLeft(), getBitmapTop());
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
                dx = 0;
                dy = 0;
                sx = event.getX();
                sy = event.getY();
                firstTouchStatus = getScalePoint(sx, sy);
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
        dx = 0;
        dy = 0;
        sx = 0;
        sy = 0;
        invalidate();
    }

    private void handleUpEventTouchScalePoint(MotionEvent event) {
        switch (firstTouchStatus) {
            case TOUCH_SCALE_POINT_LEFT_TOP:
                float scale = (cropRect.right - cropRect.left) / (cropRect.right - cropLeft);
                bitmapP1x = ((bitmapP1x - cropRect.right) * scale + cropRect.right);
                bitmapP2x = ((bitmapP2x - cropRect.right) * scale + cropRect.right);
                bitmapP3x = ((bitmapP3x - cropRect.right) * scale + cropRect.right);
                bitmapP4x = ((bitmapP4x - cropRect.right) * scale + cropRect.right);
                bitmapP1y = ((bitmapP1y - cropRect.bottom) * scale + cropRect.bottom);
                bitmapP2y = ((bitmapP2y - cropRect.bottom) * scale + cropRect.bottom);
                bitmapP3y = ((bitmapP3y - cropRect.bottom) * scale + cropRect.bottom);
                bitmapP4y = ((bitmapP4y - cropRect.bottom) * scale + cropRect.bottom);
                break;
            case TOUCH_SCALE_POINT_RIGHT_TOP:
                scale = (cropRect.right - cropRect.left) / (cropRight - cropRect.left);
                bitmapP1x = ((bitmapP1x - cropRect.left) * scale + cropRect.left);
                bitmapP2x = ((bitmapP2x - cropRect.left) * scale + cropRect.left);
                bitmapP3x = ((bitmapP3x - cropRect.left) * scale + cropRect.left);
                bitmapP4x = ((bitmapP4x - cropRect.left) * scale + cropRect.left);
                bitmapP1y = ((bitmapP1y - cropRect.bottom) * scale + cropRect.bottom);
                bitmapP2y = ((bitmapP2y - cropRect.bottom) * scale + cropRect.bottom);
                bitmapP3y = ((bitmapP3y - cropRect.bottom) * scale + cropRect.bottom);
                bitmapP4y = ((bitmapP4y - cropRect.bottom) * scale + cropRect.bottom);
                break;
            case TOUCH_SCALE_POINT_LEFT_BOTTOM:
                scale = (cropRect.right - cropRect.left) / (cropRect.right - cropLeft);
                bitmapP1x = ((bitmapP1x - cropRect.right) * scale + cropRect.right);
                bitmapP2x = ((bitmapP2x - cropRect.right) * scale + cropRect.right);
                bitmapP3x = ((bitmapP3x - cropRect.right) * scale + cropRect.right);
                bitmapP4x = ((bitmapP4x - cropRect.right) * scale + cropRect.right);
                bitmapP1y = ((bitmapP1y - cropRect.top) * scale + cropRect.top);
                bitmapP2y = ((bitmapP2y - cropRect.top) * scale + cropRect.top);
                bitmapP3y = ((bitmapP3y - cropRect.top) * scale + cropRect.top);
                bitmapP4y = ((bitmapP4y - cropRect.top) * scale + cropRect.top);
                break;
            case TOUCH_SCALE_POINT_RIGHT_BOTTOM:
                scale = (cropRect.right - cropRect.left) / (cropRight - cropRect.left);
                bitmapP1x = ((bitmapP1x - cropRect.left) * scale + cropRect.left);
                bitmapP2x = ((bitmapP2x - cropRect.left) * scale + cropRect.left);
                bitmapP3x = ((bitmapP3x - cropRect.left) * scale + cropRect.left);
                bitmapP4x = ((bitmapP4x - cropRect.left) * scale + cropRect.left);
                bitmapP1y = ((bitmapP1y - cropRect.top) * scale + cropRect.top);
                bitmapP2y = ((bitmapP2y - cropRect.top) * scale + cropRect.top);
                bitmapP3y = ((bitmapP3y - cropRect.top) * scale + cropRect.top);
                bitmapP4y = ((bitmapP4y - cropRect.top) * scale + cropRect.top);
                break;
        }
        cropLeft = cropRect.left;
        cropRight = cropRect.right;
        cropTop = cropRect.top;
        cropBottom = cropRect.bottom;
        firstTouchStatus = TOUCH_PIC;
        dx = 0;
        dy = 0;
        sx = 0;
        sy = 0;
        invalidate();
    }

    private void handleMoveEventTouchPic(MotionEvent event) {
        sx = (event.getX() - sx);
        sy = (event.getY() - sy);
        float cx = (cropRect.left + cropRect.right) / 2;
        float cy = (cropRect.top + cropRect.bottom) / 2;
        boolean canMove = true;
        if (this.rotate >= 0) {
            boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP3x + sx, bitmapP3y + sy);
            boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP2x + sx, bitmapP2y + sy);
            boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP3x + sx, bitmapP3y + sy, bitmapP4x + sx, bitmapP4y + sy);
            boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP2x + sx, bitmapP2y + sy, bitmapP4x + sx, bitmapP4y + sy);
            if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect) canMove = false;
        } else {
            boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP2x + sx, bitmapP2y + sy);
            boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP2x + sx, bitmapP2y + sy, bitmapP4x + sx, bitmapP4y + sy);
            boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP3x + sx, bitmapP3y + sy);
            boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP3x + sx, bitmapP3y + sy, bitmapP4x + sx, bitmapP4y + sy);
            if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect) canMove = false;
        }
        if (canMove) {
            bitmapP1x += sx;
            bitmapP2x += sx;
            bitmapP3x += sx;
            bitmapP4x += sx;
            bitmapP1y += sy;
            bitmapP2y += sy;
            bitmapP3y += sy;
            bitmapP4y += sy;
        }
        sx = event.getX();
        sy = event.getY();
        invalidate();
    }

    private void handleMoveEventTouchScalePoint(MotionEvent event) {
        float tempX = (event.getX() - sx);
        float tempY = (event.getY() - sy);
        switch (firstTouchStatus) {
            case TOUCH_SCALE_POINT_LEFT_TOP:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = tempX * 3 / 4;
                } else {
                    tempX = tempY * 4 / 3;
                }
                cropLeft = cropRect.left + tempX;
                cropTop = cropRect.top + tempY;
                break;
            case TOUCH_SCALE_POINT_LEFT_BOTTOM:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = -tempX * 3 / 4;
                } else {
                    tempX = -tempY * 4 / 3;
                }
                cropLeft = cropRect.left + tempX;
                cropBottom = cropRect.bottom + tempY;
                break;
            case TOUCH_SCALE_POINT_RIGHT_TOP:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = -tempX * 3 / 4;
                } else {
                    tempX = -tempY * 4 / 3;
                }
                cropRight = cropRect.right + tempX;
                cropTop = cropRect.top + tempY;
                break;
            case TOUCH_SCALE_POINT_RIGHT_BOTTOM:
                if (Math.abs(tempX * 3) >= Math.abs(tempY * 4)) {
                    tempY = tempX * 3 / 4;
                } else {
                    tempX = tempY * 4 / 3;
                }
                cropRight = cropRect.right + tempX;
                cropBottom = cropRect.bottom + tempY;
                break;
        }
        dx = event.getX();
        dy = event.getY();
        invalidate();
    }

    private float getBitmapLeft() {
        float left = Float.MAX_VALUE;
        if (bitmapP1x < left) left = bitmapP1x;
        if (bitmapP2x < left) left = bitmapP2x;
        if (bitmapP3x < left) left = bitmapP3x;
        if (bitmapP4x < left) left = bitmapP4x;
        return left;
    }

    private float getBitmapTop() {
        float top = Float.MAX_VALUE;
        if (bitmapP1y < top) top = bitmapP1y;
        if (bitmapP2y < top) top = bitmapP2y;
        if (bitmapP3y < top) top = bitmapP3y;
        if (bitmapP4y < top) top = bitmapP4y;
        return top;
    }

    private int getScalePoint(float x, float y) {
        if (leftTopCircleRect.contains(x, y)) return TOUCH_SCALE_POINT_LEFT_TOP;
        if (rightTopCircleRect.contains(x, y)) return TOUCH_SCALE_POINT_RIGHT_TOP;
        if (leftBottomRect.contains(x, y)) return TOUCH_SCALE_POINT_LEFT_BOTTOM;
        if (rightBottomRect.contains(x, y)) return TOUCH_SCALE_POINT_RIGHT_BOTTOM;
        return TOUCH_PIC;
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

    private void getRotatePoint(float rotate) {
        float dx1 = bitmapP1x - (cropRect.left + cropRect.right) / 2;
        float dy1 = bitmapP1y - (cropRect.top + cropRect.bottom) / 2;
        bitmapP1x = (float) (dx1 * Math.cos(rotate * Math.PI / 180f) - dy1 * Math.sin(rotate * Math.PI / 180f) + (cropRect.left + cropRect.right) / 2);
        bitmapP1y = (float) (dy1 * Math.cos(rotate * Math.PI / 180f) + dx1 * Math.sin(rotate * Math.PI / 180f) + (cropRect.top + cropRect.bottom) / 2);

        float dx2 = bitmapP2x - (cropRect.left + cropRect.right) / 2;
        float dy2 = bitmapP2y - (cropRect.top + cropRect.bottom) / 2;
        bitmapP2x = (float) (dx2 * Math.cos(rotate * Math.PI / 180f) - dy2 * Math.sin(rotate * Math.PI / 180f) + (cropRect.left + cropRect.right) / 2);
        bitmapP2y = (float) (dy2 * Math.cos(rotate * Math.PI / 180f) + dx2 * Math.sin(rotate * Math.PI / 180f) + (cropRect.top + cropRect.bottom) / 2);

        float dx3 = bitmapP3x - (cropRect.left + cropRect.right) / 2;
        float dy3 = bitmapP3y - (cropRect.top + cropRect.bottom) / 2;
        bitmapP3x = (float) (dx3 * Math.cos(rotate * Math.PI / 180f) - dy3 * Math.sin(rotate * Math.PI / 180f) + (cropRect.left + cropRect.right) / 2);
        bitmapP3y = (float) (dy3 * Math.cos(rotate * Math.PI / 180f) + dx3 * Math.sin(rotate * Math.PI / 180f) + (cropRect.top + cropRect.bottom) / 2);

        float dx4 = bitmapP4x - (cropRect.left + cropRect.right) / 2;
        float dy4 = bitmapP4y - (cropRect.top + cropRect.bottom) / 2;
        bitmapP4x = (float) (dx4 * Math.cos(rotate * Math.PI / 180f) - dy4 * Math.sin(rotate * Math.PI / 180f) + (cropRect.left + cropRect.right) / 2);
        bitmapP4y = (float) (dy4 * Math.cos(rotate * Math.PI / 180f) + dx4 * Math.sin(rotate * Math.PI / 180f) + (cropRect.top + cropRect.bottom) / 2);

        float cx = (cropRect.left + cropRect.right) / 2;
        float cy = (cropRect.top + cropRect.bottom) / 2;

        float scale = 1.0f;
        if (this.rotate + rotate >= 0) {
            boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
            boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
            boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
            boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
            if (isLeftTopIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y) + getTriangleHeight(cropRect.left, cropRect.top, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y), scale);
            }
            if (isLeftBottomIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.left, cropRect.bottom, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y), scale);
            }
            if (isRightTopIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y) + getTriangleHeight(cropRect.right, cropRect.top, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y), scale);
            }
            if (isRightBottomIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.right, cropRect.bottom, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y), scale);
            }
        } else {
            boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
            boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
            boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
            boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
            if (isLeftTopIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y) + getTriangleHeight(cropRect.left, cropRect.top, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y), scale);
            }
            if (isLeftBottomIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y) + getTriangleHeight(cropRect.left, cropRect.bottom, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y), scale);
            }
            if (isRightTopIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.right, cropRect.top, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y), scale);
            }
            if (isRightBottomIntersect) {
                scale = Math.max((getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.right, cropRect.bottom, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y), scale);
            }
        }
        if (scale > 1) {
            bitmapP1x = ((bitmapP1x - cx) * scale + cx);
            bitmapP2x = ((bitmapP2x - cx) * scale + cx);
            bitmapP3x = ((bitmapP3x - cx) * scale + cx);
            bitmapP4x = ((bitmapP4x - cx) * scale + cx);
            bitmapP1y = ((bitmapP1y - cy) * scale + cy);
            bitmapP2y = ((bitmapP2y - cy) * scale + cy);
            bitmapP3y = ((bitmapP3y - cy) * scale + cy);
            bitmapP4y = ((bitmapP4y - cy) * scale + cy);
        }
    }

    private float getTriangleHeight(float xTop, float yTop, float x2, float y2, float x3, float y3) {
        float size = Math.abs(0.5f * (xTop * y2 + x2 * y3 + x3 * yTop - xTop * y3 - x2 * yTop - x3 * y2));
        return (float) (size * 2 / Math.sqrt((x2 - x3) * (x2 - x3) + (y2 - y3) * (y2 - y3)));
    }

    public boolean isIntersect(double px1, double py1, double px2, double py2, double px3, double py3, double px4, double py4)//p1-p2 is or not intersect with p3-p4
    {
        boolean flag = false;
        double d = (px2 - px1) * (py4 - py3) - (py2 - py1) * (px4 - px3);
        if (d != 0) {
            double r = ((py1 - py3) * (px4 - px3) - (px1 - px3) * (py4 - py3)) / d;
            double s = ((py1 - py3) * (px2 - px1) - (px1 - px3) * (py2 - py1)) / d;
            if ((r >= 0) && (r <= 1) && (s >= 0) && (s <= 1)) {
                flag = true;
            }
        }
        return flag;
    }


}
