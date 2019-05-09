package com.jyx.countedittext;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

/**
 * 可计数的EditText
 *
 * @author Jingyongxuan
 * @date 2019/5/8
 */
public class CountEditText extends AppCompatEditText implements TextWatcher {

    private Paint mPaint;

    private OnTextOverLimitedListener mOnTextOverLimitedListener;

    private int mPaddingBottom;

    /**
     * 剩余文字数量
     */
    private int mLeftCount;

    /**
     * 限制文字数量，默认100
     */
    private int mLimitedCount;

    /**
     * 剩余文字数量提示，默认为 num/num，自定义需要用%d
     */
    private String mLeftHintText;

    /**
     * 剩余文字数量提示文字的颜色，默认和hintTextColor一致
     */
    private int mLeftHintTextColor;

    /**
     * 剩余文字数量提示文字的大小，默认和主文本大小一致
     */
    private int mLeftHintTextSize;

    public CountEditText(Context context) {
        this(context, null);
    }

    public CountEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public CountEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountEditText);

        mLimitedCount = typedArray.getInt(R.styleable.CountEditText_limitedCounts, 100);
        mLeftHintText = typedArray.getString(R.styleable.CountEditText_leftCountHintText);
        mLeftHintTextSize = (int) typedArray.getDimension(R.styleable.CountEditText_leftCountHintTextSize, getTextSize());
        mLeftHintTextColor = typedArray.getColor(R.styleable.CountEditText_leftCountHintTextColor, getCurrentHintTextColor());

        init();
    }

    private void init() {
        mPaint = new Paint();
        mLeftCount = mLimitedCount;
        if (mLeftHintText == null) {
            mLeftHintText = "%d/" + mLeftCount;
        }
        setFilters(new InputFilter[]{new InputFilter.LengthFilter(mLimitedCount)});
        addTextChangedListener(this);
        //防止输入的文字和剩余文字数量提示信息重合，底部需预留空间
        mPaddingBottom = getPaddingBottom();//获取已设置的paddingBottom
        int textHeight = (int) ((mLeftHintTextSize + 0.00000007) / 0.7535);//文字高度和textSize的关系
        setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), mPaddingBottom + textHeight + 10);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(mLeftHintTextColor);
        mPaint.setTextSize(mLeftHintTextSize);
        mPaint.setTextAlign(Paint.Align.RIGHT);
        mPaint.setTypeface(getTypeface());
        mPaint.setAntiAlias(true);//抗锯齿
        //x轴距离为view宽度减去padding距离
        float x = getWidth() - getPaddingRight();
        /*ascent为文字baseline到最高字母距离（负数），getHeight为控件高度，
        加上getScrollY为了文字超出高度可滚动时，防止剩余文字数量的提示也跟随滚动，
        再减去设置的paddingBottom值*/
        float y = mPaint.ascent() + getHeight() + getScrollY() - mPaddingBottom;
        canvas.drawText(String.format(mLeftHintText, mLeftCount), x, y, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);//按下的时候父类不拦截
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);//抬起的时候父类拦截
                break;
            case MotionEvent.ACTION_MOVE:
                //判断是否可滑动，如果可滑动则父类不拦截事件，否则拦截
                if (canVerticalScroll(this)){
                    getParent().requestDisallowInterceptTouchEvent(true);
                }else {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (getText() != null) {
            mLeftCount = mLimitedCount - getText().length();
            if (mLeftCount == 0) {
                if (mOnTextOverLimitedListener != null) {
                    mOnTextOverLimitedListener.overLimited();
                }
                return;
            }
            invalidate();
        }
    }

    /**
     * EditText竖直方向是否可以滚动
     * @param editText  需要判断的EditText
     * @return  true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() -editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if(scrollDifference == 0) {
            return false;
        }
        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    public void setOnTextOverLimitedListener(OnTextOverLimitedListener onTextOverLimitedListener) {
        mOnTextOverLimitedListener = onTextOverLimitedListener;
    }

    public interface OnTextOverLimitedListener {//用于超限后的回调

        void overLimited();
    }
}
