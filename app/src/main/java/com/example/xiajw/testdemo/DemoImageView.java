package com.example.xiajw.testdemo;

import android.animation.Animator;
import android.animation.ValueAnimator;
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
 * Test Demo Image
 * Created by xiajw on 2016/9/19.
 */
public class DemoImageView extends View {

    public interface OnSaveListener {
        void onSave();
    }

    private static final int TOUCH_PIC = 0;
    private static final int TOUCH_SCALE_POINT_LEFT_TOP = 1;
    private static final int TOUCH_SCALE_POINT_RIGHT_TOP = 2;
    private static final int TOUCH_SCALE_POINT_LEFT_BOTTOM = 3;
    private static final int TOUCH_SCALE_POINT_RIGHT_BOTTOM = 4;

    private int w, h, bw, bh;
    private Bitmap b = null;
    private Paint p = new Paint();
    private Paint pb = new Paint();
    private RectF leftTopCircleRect = new RectF();
    private RectF rightTopCircleRect = new RectF();
    private RectF leftBottomRect = new RectF();
    private RectF rightBottomRect = new RectF();
    private RectF cropRect = new RectF();
    private boolean mirror = false;
    private float rotate = 0f;

    private float angle = 0f;

    private int sx, sy, dx, dy, ex, ey;
    private boolean isReseting = false;

    private int firstTouchStatus = TOUCH_PIC;

    private int spx, spy;
    private float scalex = 1f, scaley = 1f;
    private float scaletx, scalety;

    private OnSaveListener onSaveListener;

    public DemoImageView(Context context) {
        super(context);
        init();
    }

    public DemoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DemoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setOnSaveListener(OnSaveListener listener) {
        this.onSaveListener = listener;
    }

    public void setB(Bitmap b) {
        if (this.b != null && this.b != b) {
            this.b.recycle();
        }
        this.b = b;
        bw = this.b.getWidth();
        bh = this.b.getHeight();
        float d = b.getHeight();
        angle = (float) Math.atan((d / b.getWidth()));
        cropRect.set((w - bw) / 2, (h - bh) / 2, (w + bw) / 2, (h + bh) / 2);
        invalidate();
    }

    public void mirror() {
        this.mirror = !this.mirror;
        invalidate();
    }

    public void clearBitmap() {
        if (this.b != null) {
            this.b.recycle();
            this.b = null;
        }
    }

    public void setRotate(float rotate) {
        this.rotate = rotate;
        invalidate();
    }

    public void save() {
        if (b != null) {
            Matrix m = new Matrix();
            m.postScale((float) (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)), (float) (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)), w / 2, h / 2);
            m.postRotate(rotate, w / 2, h / 2);
            Bitmap temp = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), m, false);
            int bx = (int) ((bw * (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)) * Math.cos(Math.abs(rotate) * Math.PI / 180f) + bh * (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)) * Math.sin(Math.abs(rotate) * Math.PI / 180f) - bw) * 0.5f);
            int by = (int) ((bw * (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)) * Math.sin(Math.abs(rotate) * Math.PI / 180f) + bh * (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)) * Math.cos(Math.abs(rotate) * Math.PI / 180f) - bh) * 0.5f);
            rotate = 0f;
            mirror = false;
            isReseting = false;
            sx = 0;
            sy = 0;
            dx = 0;
            dy = 0;
            ex = 0;
            ey = 0;
            if (temp != null && temp != b) {
                Bitmap newB = Bitmap.createBitmap(temp, bx, by, b.getWidth(), b.getHeight(), new Matrix(), false);
                temp.recycle();
                temp = null;
                setB(newB);
            } else {
                setB(temp);
            }

            if (onSaveListener != null) {
                onSaveListener.onSave();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        w = getMeasuredWidth();
        h = getMeasuredWidth() * 4 / 3;
        setMeasuredDimension(w, h);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        int radius = 20;
        int left = (w - bw) / 2;
        int top = (h - bh) / 2;
        int right = (w + bw) / 2;
        int bottom = (h + bh) / 2;
        float cropleft = cropRect.left;
        float cropRight = cropRect.right;
        float cropTop = cropRect.top;
        float cropBottom = cropRect.bottom;
        switch (firstTouchStatus) {
            case TOUCH_PIC:
                break;
            case TOUCH_SCALE_POINT_LEFT_TOP:
                left = spx;
                top = spy;
                cropleft = spx;
                cropTop = spy;
                break;
            case TOUCH_SCALE_POINT_RIGHT_TOP:
                right = spx;
                top = spy;
                cropRight = spx;
                cropTop = spy;
                break;
            case TOUCH_SCALE_POINT_LEFT_BOTTOM:
                left = spx;
                bottom = spy;
                cropleft = spx;
                cropBottom = spy;
                break;
            case TOUCH_SCALE_POINT_RIGHT_BOTTOM:
                right = spx;
                bottom = spy;
                cropRight = spx;
                cropBottom = spy;
                break;
        }

        if (b != null) {
            Matrix matrix = new Matrix();
            matrix.postTranslate(w / 2 - b.getWidth() / 2, h / 2 - b.getHeight() / 2);
            matrix.postScale((float) (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)), (float) (Math.sin(Math.abs(rotate) * Math.PI / 180f + angle) / Math.sin(angle)), w / 2, h / 2);
            matrix.postRotate(rotate, w / 2, h / 2);
            matrix.postTranslate(-scaletx, -scalety);
            matrix.postScale(scalex, scaley, w / 2, h / 2);
            if (mirror) matrix.postScale(-1, 1, w / 2, h / 2);//前两个是xy变换，后两个是对称轴中心点
            matrix.postTranslate(dx, dy);
            canvas.drawBitmap(b, matrix, p);
        }
        canvas.drawLine(left, top, right, top, pb);
        canvas.drawLine(right, top, right, bottom, pb);
        canvas.drawLine(right, bottom, left, bottom, pb);
        canvas.drawLine(left, bottom, left, top, pb);

        leftTopCircleRect.set(cropleft - radius, cropTop - radius,
                cropleft + radius, cropTop + radius);
        rightTopCircleRect.set(cropRight - radius, cropTop - radius,
                cropRight + radius, cropTop + radius);
        leftBottomRect.set(cropleft - radius, cropBottom - radius,
                cropleft + radius, cropBottom + radius);
        rightBottomRect.set(cropRight - radius, cropBottom - radius,
                cropRight + radius, cropBottom + radius);
        canvas.drawRect(leftTopCircleRect, pb);
        canvas.drawRect(rightTopCircleRect, pb);
        canvas.drawRect(leftBottomRect, pb);
        canvas.drawRect(rightBottomRect, pb);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isReseting) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    dx = 0;
                    dy = 0;
                    ex = 0;
                    ey = 0;
                    sx = (int) event.getX();
                    sy = (int) event.getY();
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
        return super.onTouchEvent(event);
    }

    private void init() {
        setBackgroundColor(Color.GRAY);
        pb.setColor(Color.WHITE);
        pb.setStrokeWidth(5);
        leftTopCircleRect = new RectF(0, 0, 40, 40);
        rightTopCircleRect = new RectF(leftTopCircleRect);
        leftBottomRect = new RectF(leftTopCircleRect);
        rightBottomRect = new RectF(leftTopCircleRect);
    }

    private int getScalePoint(int x, int y) {
        if (leftTopCircleRect.contains(x, y)) return TOUCH_SCALE_POINT_LEFT_TOP;
        if (rightTopCircleRect.contains(x, y)) return TOUCH_SCALE_POINT_RIGHT_TOP;
        if (leftBottomRect.contains(x, y)) return TOUCH_SCALE_POINT_LEFT_BOTTOM;
        if (rightBottomRect.contains(x, y)) return TOUCH_SCALE_POINT_RIGHT_BOTTOM;
        return TOUCH_PIC;
    }

    private void handleMoveEventTouchPic(MotionEvent event) {
        dx = (int) (event.getX() - sx);
        dy = (int) (event.getY() - sy);
        spx = 0;
        spy = 0;
        invalidate();
    }

    private void handleMoveEventTouchScalePoint(MotionEvent event) {
        spx = (int) event.getX();
        spy = (int) event.getY();
        dx = 0;
        dy = 0;
        invalidate();
    }

    private void handleUpEventTouchPic(MotionEvent event) {
        isReseting = true;
        dx = (int) (event.getX() - sx);
        dy = (int) (event.getY() - sy);
        ex = dx;
        ey = dy;
        ValueAnimator animator = ValueAnimator.ofFloat(1f, 0f);
        animator.setDuration(200);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                dx = (int) (ex * factor);
                dy = (int) (ey * factor);
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isReseting = false;
                firstTouchStatus = TOUCH_PIC;
                dx = 0;
                dy = 0;
                sx = 0;
                sy = 0;
                ex = 0;
                ey = 0;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void handleUpEventTouchScalePoint(MotionEvent event) {

        switch (firstTouchStatus) {
            case TOUCH_SCALE_POINT_LEFT_TOP:
                scaletx = (((w + bw) * 0.5f + spx) * 0.5f - w * 0.5f) / scalex + scaletx;
                scalety = (((h + bh) * 0.5f + spy) * 0.5f - h * 0.5f) / scaley + scalety;
                scalex = bw / ((w + bw) * 0.5f - spx) * scalex;
                scaley = bh / ((h + bh) * 0.5f - spy) * scaley;
                break;
            case TOUCH_SCALE_POINT_RIGHT_TOP:
                scaletx = (((w - bw) * 0.5f + spx) * 0.5f - w * 0.5f) / scalex + scaletx;
                scalety = (((h + bh) * 0.5f + spy) * 0.5f - h * 0.5f) / scaley + scalety;
                scalex = bw / (spx - (w - bw) * 0.5f) * scalex;
                scaley = bh / ((h + bh) * 0.5f - spy) * scaley;
                break;
            case TOUCH_SCALE_POINT_LEFT_BOTTOM:
                scaletx = (((w + bw) * 0.5f + spx) * 0.5f - w * 0.5f) / scalex + scaletx;
                scalety = (((h - bh) * 0.5f + spy) * 0.5f - h * 0.5f) / scaley + scalety;
                scalex = bw / ((w + bw) * 0.5f - spx) * scalex;
                scaley = bh / (spy - (h - bh) * 0.5f) * scaley;
                break;
            case TOUCH_SCALE_POINT_RIGHT_BOTTOM:
                scaletx = (((w - bw) * 0.5f + spx) * 0.5f - w * 0.5f) / scalex + scaletx;
                scalety = (((h - bh) * 0.5f + spy) * 0.5f - h * 0.5f) / scaley + scalety;
                scalex = bw / (spx - (w - bw) * 0.5f) * scalex;
                scaley = bh / (spy - (h - bh) * 0.5f) * scaley;
                break;
        }

        isReseting = false;
        firstTouchStatus = TOUCH_PIC;
        dx = 0;
        dy = 0;
        sx = 0;
        sy = 0;
        ex = 0;
        ey = 0;

        invalidate();

    }
}
