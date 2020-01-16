package com.jyx.moveborderline;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MoveLineFrameLayout extends FrameLayout {

    private Path mPath1;
    private Path mPath2;
    private Path mDst1;
    private Path mDst2;
    private Path mDst3;
    private Path mDst4;
    private Paint mPaint;
    private int width;
    private int height;
    private float mLength;
    private PathMeasure mPathMeasure1;
    private PathMeasure mPathMeasure2;
    private PathMeasure mPathMeasure3;
    private PathMeasure mPathMeasure4;
    private int a;
    private int b;
    private ValueAnimator valueAnimator;
    private int speed = 20;
    private float lineWidth = 40;
    private boolean isInit;

    public MoveLineFrameLayout(@NonNull Context context) {
        this(context, null);
    }

    public MoveLineFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MoveLineFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAnim();
    }

    private void initAnim() {
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        mPathMeasure1 = new PathMeasure();
        mPathMeasure2 = new PathMeasure();
        mPathMeasure3 = new PathMeasure();
        mPathMeasure4 = new PathMeasure();

        mPath1 = new Path();
        mPath2 = new Path();
        mDst1 = new Path();
        mDst2 = new Path();
        mDst3 = new Path();
        mDst4 = new Path();

        valueAnimator = ValueAnimator.ofArgb(Color.RED, Color.BLUE);
        valueAnimator.setDuration(1000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int color = (int) animation.getAnimatedValue();
                mPaint.setColor(color);
                invalidate();
                updateA();
            }
        });
        setWillNotDraw(true);
//        valueAnimator.start();
    }

    public void startMove() {
        isInit = true;
        setWillNotDraw(false);
        valueAnimator.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (isInit){
            width = getWidth();
            height = getHeight();
            mPath1.moveTo(0, 0);
            mPath1.lineTo(0, height);
            mPath1.lineTo(width, height);

            mPath2.moveTo(width, height);
            mPath2.lineTo(width, 0);
            mPath2.lineTo(0, 0);

            mPathMeasure1.setPath(mPath1, false);
            mPathMeasure2.setPath(mPath2, false);
            mPathMeasure3.setPath(mPath1, false);
            mPathMeasure4.setPath(mPath2, false);
            mLength = height / 2 + width / 2;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mDst1.reset();
        mPathMeasure1.getSegment(0 + a, mLength + a, mDst1, true);
        canvas.drawPath(mDst1, mPaint);

        mDst2.reset();
        mPathMeasure2.getSegment(0 + a, mLength + a, mDst2, true);
        canvas.drawPath(mDst2, mPaint);

        if (a >= mLength) {
            mDst3.reset();
            mPathMeasure3.getSegment(0, b, mDst3, true);
            canvas.drawPath(mDst3, mPaint);

            mDst4.reset();
            mPathMeasure4.getSegment(0, b, mDst4, true);
            canvas.drawPath(mDst4, mPaint);
            b += speed;
        } else {
            b = 0;
        }

    }

    private void updateA() {
        a += speed;
        if (a >= mLength * 2) {
            a = 0;
        }
    }
}
