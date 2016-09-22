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
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by xiajw on 2016/9/21.
 */

public class DemoImageViewStable4 extends View implements ScaleGestureDetector.OnScaleGestureListener {

    private static final int SCALE_POINT_RADIUS = 60;

    private static final int TOUCH_PIC = 0;
    private static final int TOUCH_SCALE_POINT_LEFT_TOP = 1;
    private static final int TOUCH_SCALE_POINT_RIGHT_TOP = 2;
    private static final int TOUCH_SCALE_POINT_LEFT_BOTTOM = 3;
    private static final int TOUCH_SCALE_POINT_RIGHT_BOTTOM = 4;

    private ScaleGestureDetector mScaleDetector;
    private boolean isScaling = false;

    private Bitmap bitmap = null, originalBit, startBitmap;
    private Paint bitmapPaint = new Paint();
    private Paint cropPaint = new Paint();
    private Paint maskPaint = new Paint();
    private RectF leftTopCircleRect = new RectF();
    private RectF rightTopCircleRect = new RectF();
    private RectF leftBottomRect = new RectF();
    private RectF rightBottomRect = new RectF();
    private RectF cropRect = new RectF();
    private int originWidth, originHeight;

    private float initBitmapLeft, initBitmapRight, initBitmapTop, initBitmapBottom;

    private int firstTouchStatus = TOUCH_PIC;

    private float bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y;
    private float cropTop, cropLeft, cropRight, cropBottom;

    private float dx, dy;
    private float sx, sy;

    private float rotate = 0f;
    private boolean mirror = false;

    private float ratio = 1f;

    public DemoImageViewStable4(Context context) {
        super(context);
        init(context);
    }

    public DemoImageViewStable4(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DemoImageViewStable4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void initOriginBit(Bitmap originalBit) {
        this.originalBit = originalBit;
    }

    public void initBitmap(Bitmap b, float ratio) {
        if (bitmap != null && bitmap != b && bitmap != originalBit) {
            bitmap.recycle();
        }
        bitmap = b;
        startBitmap = b;
        originWidth = b.getWidth();
        originHeight = b.getHeight();
        int bitmapLeft = (getMeasuredWidth() - bitmap.getWidth()) / 2;
        int bitmapWidth = bitmap.getWidth();
        if (ratio >= (float)(originWidth) / originHeight) {
            if (ratio >= 3f / 4) {
                cropLeft = 20;
                cropRight = getWidth() - 20;
                cropTop = getHeight() / 2 - (float) (getWidth() - 40) / ratio / 2;
                cropBottom = getHeight() / 2 + (float) (getWidth() - 40) / ratio / 2;
                cropRect.set(cropLeft, cropTop, cropRight, cropBottom);
                bitmapP1x = cropLeft;
                bitmapP1y = getHeight() / 2 - (cropRight - cropLeft) * bitmap.getHeight() / bitmap.getWidth() / 2;
                bitmapP2x = cropRight;
                bitmapP2y = getHeight() / 2 - (cropRight - cropLeft) * bitmap.getHeight() / bitmap.getWidth() / 2;
                bitmapP3x = cropLeft;
                bitmapP3y = getHeight() / 2 + (cropRight - cropLeft) * bitmap.getHeight() / bitmap.getWidth() / 2;
                bitmapP4x = cropRight;
                bitmapP4y = getHeight() / 2 + (cropRight - cropLeft) * bitmap.getHeight() / bitmap.getWidth() / 2;
            } else {
                cropLeft = getWidth() / 2 - (float)(getHeight() - 40) * ratio / 2;
                cropRight = getWidth() / 2 + (float)(getHeight() - 40) * ratio / 2;
                cropTop = 20;
                cropBottom = getHeight() - 20;
                cropRect.set(cropLeft, cropTop, cropRight, cropBottom);
                bitmapP1x = getWidth() / 2 - (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP1y = 20;
                bitmapP2x = getWidth() / 2 + (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP2y = 20;
                bitmapP3x = getWidth() / 2 - (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP3y = getHeight() - 20;
                bitmapP4x = getWidth() / 2 + (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP4y = getHeight() - 20;
            }
        } else {
            if (ratio >= 3f / 4) {
                cropLeft = 20;
                cropRight = getWidth() - 20;
                cropTop = getHeight() / 2 - (float) (getWidth() - 40) / ratio / 2;
                cropBottom = getHeight() / 2 + (float) (getWidth() - 40) / ratio / 2;
                cropRect.set(cropLeft, cropTop, cropRight, cropBottom);
                bitmapP1x = getWidth() / 2 - (cropBottom - cropTop) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP1y = cropTop;
                bitmapP2x = getWidth() / 2 + (cropBottom - cropTop) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP2y = cropTop;
                bitmapP3x = getWidth() / 2 - (cropBottom - cropTop) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP3y = cropBottom;
                bitmapP4x = getWidth() / 2 + (cropBottom - cropTop) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP4y = cropBottom;
            } else {
                cropLeft = getWidth() / 2 - (float)(getHeight() - 40) * ratio / 2;
                cropRight = getWidth() / 2 + (float)(getHeight() - 40) * ratio / 2;
                cropTop = 20;
                cropBottom = getHeight() - 20;
                cropRect.set(cropLeft, cropTop, cropRight, cropBottom);
                bitmapP1x = getWidth() / 2 - (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP1y = 20;
                bitmapP2x = getWidth() / 2 + (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP2y = 20;
                bitmapP3x = getWidth() / 2 - (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP3y = getHeight() - 20;
                bitmapP4x = getWidth() / 2 + (float)(getHeight() - 40) * bitmap.getWidth() / bitmap.getHeight() / 2;
                bitmapP4y = getHeight() - 20;
            }
        }
        initBitmapLeft = bitmapP1x;
        initBitmapRight = bitmapP2x;
        initBitmapTop = bitmapP1y;
        initBitmapBottom = bitmapP3y;

        this.ratio = ratio;
        dx = 0;
        dy = 0;
        sx = 0;
        sy = 0;
        rotate = 0f;
        mirror = false;
        invalidate();
    }

    public void setRotate(float rotate) {
        Matrix m = new Matrix();
        if (!mirror)
            m.postRotate(rotate);
        else
            m.postRotate(-rotate);
        Bitmap newBitmap = Bitmap.createBitmap(startBitmap, 0, 0, startBitmap.getWidth(), startBitmap.getHeight(), m, false);
        if (newBitmap != null && newBitmap != bitmap && bitmap != startBitmap && bitmap != originalBit) {
            bitmap.recycle();
        }
        bitmap = newBitmap;
        if (!mirror) {
            getRotatePoint(rotate - this.rotate);
            this.rotate = rotate;
        } else {
            getRotatePoint(-rotate - this.rotate);
            this.rotate = -rotate;
        }
        invalidate();
    }

    public void clearBitmap() {
        if (startBitmap != null && startBitmap != bitmap && startBitmap != originalBit) {
            startBitmap.recycle();
            startBitmap = null;
        }
        if (originalBit != null && originalBit != bitmap) {
            originalBit.recycle();
            originalBit = null;
        }
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    public void save() {
        int left = (int) ((cropLeft - getBitmapLeft()) / (getBitmapRight() - getBitmapLeft()) * bitmap.getWidth());
        int top = (int) ((cropTop - getBitmapTop()) / (getBitmapBottom() - getBitmapTop()) * bitmap.getHeight());
        int width = (int) ((cropRight - cropLeft) / (getBitmapRight() - getBitmapLeft()) * bitmap.getWidth());
        int height = (int) ((cropBottom - cropTop) / (getBitmapBottom() - getBitmapTop()) * bitmap.getHeight());
        Bitmap saveBit = Bitmap.createBitmap(bitmap, left, top, width, height);
        initBitmap(saveBit, (float)saveBit.getWidth() / saveBit.getHeight());
    }

    public void mirror() {
        Matrix om = new Matrix();
        om.postScale(-1, 1, startBitmap.getWidth() / 2, startBitmap.getHeight() / 2);
        Bitmap newOrigin = Bitmap.createBitmap(startBitmap, 0, 0, startBitmap.getWidth(), startBitmap.getHeight(), om, false);
        if (newOrigin != null && newOrigin != startBitmap && startBitmap != originalBit) {
            startBitmap.recycle();
        }
        startBitmap = newOrigin;
        Matrix m = new Matrix();
        m.postRotate(-rotate);
        Bitmap newBitmap = Bitmap.createBitmap(startBitmap, 0, 0, startBitmap.getWidth(), startBitmap.getHeight(), m, false);
        if (newBitmap != null && bitmap != newBitmap && bitmap != startBitmap && bitmap != originalBit) {
            bitmap.recycle();
        }
        bitmap = newBitmap;
        bitmapP1x = (cropRect.right + cropRect.left) - bitmapP1x;
        bitmapP2x = (cropRect.right + cropRect.left) - bitmapP2x;
        bitmapP3x = (cropRect.right + cropRect.left) - bitmapP3x;
        bitmapP4x = (cropRect.right + cropRect.left) - bitmapP4x;
        rotate = -rotate;
        mirror = !mirror;
        invalidate();
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
        canvas.drawRect(0, 0, getWidth(), cropTop, maskPaint);
        canvas.drawRect(0, cropTop, cropLeft, cropBottom, maskPaint);
        canvas.drawRect(cropRight, cropTop, getWidth(), cropBottom, maskPaint);
        canvas.drawRect(0, cropBottom, getWidth(), getHeight(), maskPaint);
        canvas.drawRect(leftTopCircleRect, cropPaint);
        canvas.drawRect(rightTopCircleRect, cropPaint);
        canvas.drawRect(leftBottomRect, cropPaint);
        canvas.drawRect(rightBottomRect, cropPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        if (isScaling) return true;
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
        if (Math.abs(sx) < 2) sx = 0;
        if (Math.abs(sy) < 2) sy = 0;
        float cx = (cropRect.left + cropRect.right) / 2;
        float cy = (cropRect.top + cropRect.bottom) / 2;
        boolean canMove = true;
        if (this.rotate >= 0) {
            if (!mirror) {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP3x + sx, bitmapP3y + sy);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP2x + sx, bitmapP2y + sy);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP3x + sx, bitmapP3y + sy, bitmapP4x + sx, bitmapP4y + sy);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP2x + sx, bitmapP2y + sy, bitmapP4x + sx, bitmapP4y + sy);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canMove = false;
            } else {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP2x + sx, bitmapP2y + sy, bitmapP4x + sx, bitmapP4y + sy);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP2x + sx, bitmapP2y + sy);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP3x + sx, bitmapP3y + sy, bitmapP4x + sx, bitmapP4y + sy);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP3x + sx, bitmapP3y + sy);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canMove = false;
            }
        } else {
            if (!mirror) {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP2x + sx, bitmapP2y + sy);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP2x + sx, bitmapP2y + sy, bitmapP4x + sx, bitmapP4y + sy);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP3x + sx, bitmapP3y + sy);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP3x + sx, bitmapP3y + sy, bitmapP4x + sx, bitmapP4y + sy);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canMove = false;
            } else {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP2x + sx, bitmapP2y + sy);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x + sx, bitmapP1y + sy, bitmapP3x + sx, bitmapP3y + sy);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP2x + sx, bitmapP2y + sy, bitmapP4x + sx, bitmapP4y + sy);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP3x + sx, bitmapP3y + sy, bitmapP4x + sx, bitmapP4y + sy);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canMove = false;
            }
        }
        if (Math.abs(rotate) < 1e-6) {
            Log.d("MyLog", "sx = " + sx + " sy = " + sy);
            if (!mirror) {
                if (bitmapP1x + sx <= cropLeft && bitmapP2x + sx >= cropRight && bitmapP1y + sy <= cropTop && bitmapP3y + sy >= cropBottom) {
                    canMove = true;
                }
            } else {
                if (bitmapP2x + sx <= cropLeft && bitmapP1x + sx >= cropRight && bitmapP2y + sy <= cropTop && bitmapP4y + sy >= cropBottom) {
                    canMove = true;
                }
            }
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
        float cx = (cropRect.left + cropRect.right) / 2;
        float cy = (cropRect.top + cropRect.bottom) / 2;
        switch (firstTouchStatus) {
            case TOUCH_SCALE_POINT_LEFT_TOP:
                if (Math.abs(tempX) >= Math.abs(tempY * ratio)) {
                    tempY = tempX / ratio;
                } else {
                    tempX = tempY * ratio;
                }
                if (tempX < 0 && cropLeft + tempX < 0) {
                    tempX = -cropLeft;
                    tempY = tempX / ratio;
                } else if (tempX > 0 && cropLeft + tempX >= cropRect.right) {
                    tempX = cropRect.right - cropLeft;
                    tempY = tempX / ratio;
                }
                boolean canScale = true;
                if (this.rotate + rotate >= 0) {
                    if (!mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropTop + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                } else {
                    if (!mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropTop + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropRect.bottom, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropRect.bottom, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                }
                if (Math.abs(rotate) < 1e-6) {
                    if (cropLeft + tempX < cropRect.right && cropLeft + tempX >= 0 && cropTop + tempY  < cropRect.bottom && cropTop + tempY >= 0) {
                        canScale = (cropLeft + tempX > (!mirror? bitmapP1x : bitmapP2x)
                                && (cropLeft + tempX < (!mirror? bitmapP2x : bitmapP1x))
                                && cropTop + tempY < bitmapP3y
                                && cropTop + tempY > bitmapP1y);
                    } else {
                        canScale = false;
                    }
                }
                if (canScale) {
                    cropLeft = cropLeft + tempX;
                    cropTop = cropTop + tempY;
                }
                break;
            case TOUCH_SCALE_POINT_LEFT_BOTTOM:
                if (Math.abs(tempX) >= Math.abs(tempY * ratio)) {
                    tempY = -tempX / ratio;
                } else {
                    tempX = -tempY * ratio;
                }
                if (tempX < 0 && cropLeft + tempX < 0) {
                    tempX = -cropLeft;
                    tempY = tempX / ratio;
                } else if (tempX > 0 && cropLeft + tempX >= cropRect.right) {
                    tempX = cropRect.right - cropLeft;
                    tempY = tempX / ratio;
                }
                canScale = true;
                if (this.rotate + rotate >= 0) {
                    if (!mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropBottom + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropRect.top, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropBottom + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                } else {
                    if (!mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropBottom + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropLeft + tempX, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isLeftBottomIntersect = isIntersect(cropLeft + tempX, cropBottom + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRect.right, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                }
                if (Math.abs(rotate) < 1e-6) {
                    if (cropLeft + tempX < cropRect.right && cropLeft + tempX >= 0 && cropBottom + tempY > cropRect.top && cropBottom + tempY <= getHeight()) {
                        canScale = (cropLeft + tempX > (!mirror? bitmapP1x : bitmapP2x)
                                && (cropLeft + tempX < (!mirror? bitmapP2x : bitmapP1x))
                                && cropBottom + tempY < bitmapP3y
                                && cropBottom + tempY > bitmapP1y);
                    } else {
                        canScale = false;
                    }
                }
                if (canScale) {
                    cropLeft = cropLeft + tempX;
                    cropBottom = cropBottom + tempY;
                }
                break;
            case TOUCH_SCALE_POINT_RIGHT_TOP:
                if (Math.abs(tempX) >= Math.abs(tempY * ratio)) {
                    tempY = -tempX / ratio;
                } else {
                    tempX = -tempY * ratio;
                }
                if (tempX > 0 && cropRight + tempX > getWidth()) {
                    tempX = getWidth() - cropRight;
                    tempY = tempX / ratio;
                } else if (tempX < 0 && cropRight + tempX <= cropRect.left) {
                    tempX = cropRect.left - cropRight;
                    tempY = tempX / ratio;
                }
                canScale = true;
                if (this.rotate + rotate >= 0) {
                    if (mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropRect.bottom, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropTop + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropRect.bottom, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                } else {
                    if (!mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropTop + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropTop + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                }
                if (Math.abs(rotate) < 1e-6) {
                    if (cropRight + tempX > cropRect.left && cropRight + tempX <= getWidth() && cropTop + tempY < cropRect.bottom && cropTop + tempY >= 0) {
                        canScale = (cropRight + tempX > (!mirror? bitmapP1x : bitmapP2x)
                                && (cropRight + tempX < (!mirror? bitmapP2x : bitmapP1x))
                                && cropTop + tempY < bitmapP3y
                                && cropTop + tempY > bitmapP1y);
                    } else {
                        canScale = false;
                    }
                }
                if (canScale) {
                    cropRight = cropRight + tempX;
                    cropTop = cropTop + tempY;
                }
                break;
            case TOUCH_SCALE_POINT_RIGHT_BOTTOM:
                if (Math.abs(tempX) >= Math.abs(tempY * ratio)) {
                    tempY = tempX / ratio;
                } else {
                    tempX = tempY * ratio;
                }
                if (tempX > 0 && cropRight + tempX > getWidth()) {
                    tempX = getWidth() - cropRight;
                    tempY = tempX / ratio;
                } else if (tempX < 0 && cropRight + tempX <= cropRect.left) {
                    tempX = cropRect.left - cropRight;
                    tempY = tempX / ratio;
                }
                canScale = true;
                if (this.rotate + rotate >= 0) {
                    if (!mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropBottom + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropBottom + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                } else {
                    if (!mirror) {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropRect.top, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropBottom + tempY, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    } else {
                        boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                        boolean isRightTopIntersect = isIntersect(cropRight + tempX, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                        boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropBottom + tempY, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                        boolean isRightBottomIntersect = isIntersect(cropRight + tempX, cropBottom + tempY, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                        if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                            canScale = false;
                    }
                }
                if (Math.abs(rotate) < 1e-6)  {
                    if (cropRight + tempX > cropRect.left && cropRight + tempX <= getWidth() && cropBottom + tempY > cropRect.top && cropBottom + tempY <= getHeight()){
                        canScale = (cropRight + tempX > (!mirror? bitmapP1x : bitmapP2x)
                                && (cropRight + tempX < (!mirror? bitmapP2x : bitmapP1x))
                                && cropBottom + tempY < bitmapP3y
                                && cropBottom + tempY > bitmapP1y);
                    } else {
                        canScale = false;
                    }
                }
                if (canScale) {
                    cropRight = cropRight + tempX;
                    cropBottom = cropBottom + tempY;
                }
                break;
        }
        sx = event.getX();
        sy = event.getY();
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

    private float getBitmapRight() {
        float right = Float.MIN_VALUE;
        if (bitmapP1x > right) right = bitmapP1x;
        if (bitmapP2x > right) right = bitmapP2x;
        if (bitmapP3x > right) right = bitmapP3x;
        if (bitmapP4x > right) right = bitmapP4x;
        return right;
    }

    private float getBitmapBottom() {
        float bottom = Float.MIN_VALUE;
        if (bitmapP1y > bottom) bottom = bitmapP1y;
        if (bitmapP2y > bottom) bottom = bitmapP2y;
        if (bitmapP3y > bottom) bottom = bitmapP3y;
        if (bitmapP4y > bottom) bottom = bitmapP4y;
        return bottom;
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

    private void init(Context context) {
        mScaleDetector = new ScaleGestureDetector(context, this);
        setBackgroundColor(Color.GRAY);
        cropPaint.setColor(Color.WHITE);
        cropPaint.setStrokeWidth(5);
        maskPaint.setColor(Color.parseColor("#b0000000"));
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
            if (!mirror) {
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
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                if (isLeftTopIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.left, cropRect.top, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y), scale);
                }
                if (isLeftBottomIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.left, cropRect.bottom, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y), scale);
                }
                if (isRightTopIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y) + getTriangleHeight(cropRect.right, cropRect.top, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y), scale);
                }
                if (isRightBottomIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y) + getTriangleHeight(cropRect.right, cropRect.bottom, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y), scale);
                }
            }
        } else {
            if (!mirror) {
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
            } else {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y);
                if (isLeftTopIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y) + getTriangleHeight(cropRect.left, cropRect.top, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP2x, bitmapP2y), scale);
                }
                if (isLeftBottomIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.left, cropRect.bottom, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP2x, bitmapP2y, bitmapP4x, bitmapP4y), scale);
                }
                if (isRightTopIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y) + getTriangleHeight(cropRect.right, cropRect.top, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y)) / getTriangleHeight(cx, cy, bitmapP1x, bitmapP1y, bitmapP3x, bitmapP3y), scale);
                }
                if (isRightBottomIntersect) {
                    scale = Math.max((getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y) + getTriangleHeight(cropRect.right, cropRect.bottom, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y)) / getTriangleHeight(cx, cy, bitmapP3x, bitmapP3y, bitmapP4x, bitmapP4y), scale);
                }
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

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = detector.getScaleFactor();
        float cx = (cropRect.left + cropRect.right) / 2;
        float cy = (cropRect.top + cropRect.bottom) / 2;
        float tbitmapP1x = ((bitmapP1x - cx) * scale + cx);
        float tbitmapP2x = ((bitmapP2x - cx) * scale + cx);
        float tbitmapP3x = ((bitmapP3x - cx) * scale + cx);
        float tbitmapP4x = ((bitmapP4x - cx) * scale + cx);
        float tbitmapP1y = ((bitmapP1y - cy) * scale + cy);
        float tbitmapP2y = ((bitmapP2y - cy) * scale + cy);
        float tbitmapP3y = ((bitmapP3y - cy) * scale + cy);
        float tbitmapP4y = ((bitmapP4y - cy) * scale + cy);
        boolean canScale = true;
        if (this.rotate >= 0) {
            if (!mirror) {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP3x, tbitmapP3y);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP2x, tbitmapP2y);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, tbitmapP3x, tbitmapP3y, tbitmapP4x, tbitmapP4y);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, tbitmapP2x, tbitmapP2y, tbitmapP4x, tbitmapP4y);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canScale = false;
            } else {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, tbitmapP2x, tbitmapP2y, tbitmapP4x, tbitmapP4y);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP2x, tbitmapP2y);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, tbitmapP3x, tbitmapP3y, tbitmapP4x, tbitmapP4y);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP3x, tbitmapP3y);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canScale = false;
            }
        } else {
            if (!mirror) {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP2x, tbitmapP2y);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, tbitmapP2x, tbitmapP2y, tbitmapP4x, tbitmapP4y);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP3x, tbitmapP3y);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, tbitmapP3x, tbitmapP3y, tbitmapP4x, tbitmapP4y);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canScale = false;
            } else {
                boolean isLeftTopIntersect = isIntersect(cropRect.left, cropRect.top, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP2x, tbitmapP2y);
                boolean isRightTopIntersect = isIntersect(cropRect.right, cropRect.top, cx, cy, tbitmapP1x, tbitmapP1y, tbitmapP3x, tbitmapP3y);
                boolean isLeftBottomIntersect = isIntersect(cropRect.left, cropRect.bottom, cx, cy, tbitmapP2x, tbitmapP2y, tbitmapP4x, tbitmapP4y);
                boolean isRightBottomIntersect = isIntersect(cropRect.right, cropRect.bottom, cx, cy, tbitmapP3x, tbitmapP3y, tbitmapP4x, tbitmapP4y);
                if (isLeftTopIntersect | isLeftBottomIntersect | isRightTopIntersect | isRightBottomIntersect)
                    canScale = false;
            }
        }
        if (canScale) {
            bitmapP1x = tbitmapP1x;
            bitmapP2x = tbitmapP2x;
            bitmapP3x = tbitmapP3x;
            bitmapP4x = tbitmapP4x;
            bitmapP1y = tbitmapP1y;
            bitmapP2y = tbitmapP2y;
            bitmapP3y = tbitmapP3y;
            bitmapP4y = tbitmapP4y;
        }
        invalidate();
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        isScaling = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isScaling = false;
            }
        }, 200);
    }

    private boolean isAtOriginalLeftTop() {
        if (!mirror) {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP1x <= initBitmapLeft && bitmapP1y <= initBitmapTop);
        } else {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP2x <= initBitmapLeft && bitmapP2y <= initBitmapTop);
        }
    }

    private boolean isAtOriginalLeftBottom() {
        if (!mirror) {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP3x <= initBitmapLeft && bitmapP3y >= initBitmapBottom);
        } else {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP4x <= initBitmapLeft && bitmapP4y >= initBitmapBottom);
        }
    }

    private boolean isAtOriginalRightTop() {
        if (!mirror) {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP2x >= initBitmapRight && bitmapP2y <= initBitmapTop);
        } else {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP1x >= initBitmapRight && bitmapP1y <= initBitmapTop);
        }
    }

    private boolean isAtOriginalRightBottom() {
        if (!mirror) {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP4x >= initBitmapRight && bitmapP4y >= initBitmapBottom);
        } else {
            return (Math.abs(this.rotate) < 1e-6 && bitmapP3x >= initBitmapRight && bitmapP3y >= initBitmapBottom);
        }
    }

}
