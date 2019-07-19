package com.jyx.barrageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.*;

public class BarrageView extends RelativeLayout {
    private Set<Integer> existLineCount;
    private int mRowNum = 6;
    private long mDuration = 5000;//弹幕在屏幕显示的时间 默认5s
    private int mAlpha = 180;//背景的透明度0-255
    private int mDirection = FROM_RIGNG_TO_LEFT;//当前弹幕活动方向 默认从右到左
    public static final int FROM_LEFT_TO_RIGHT = 1;//从左到右
    public static final int FROM_RIGNG_TO_LEFT = 2;//从右到左
    private int mScreenWidth;
    private List<TextView> mChildView;
    private LinkedList<Integer> mRowPosList;
    public BarrageView(Context context) {
        super(context);
        init();
    }
    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    public BarrageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        mScreenWidth = DpPxUtils.getScreenWidth();
        mChildView = new ArrayList<>();
        mRowPosList = new LinkedList();
        existLineCount = new HashSet<>();
    }
    /**
     * 设置弹幕飘动方向
     * @param direction 弹幕飘动方向 默认从右到左 FROM_RIGNG_TO_LEFT
     */
    public void setDirection(int direction){
        mDirection = direction;
    }
    /**
     * 设置弹幕飘屏时间
     * @param duration 弹幕飘屏时间 默认5s
     */
    public void setDuration(long duration){
        mDuration = duration;
    }
    /**
     * 设置飘屏行数
     * @param rowNum 飘屏行数 默认6条
     */
    public void setRowNum(int rowNum){
        mRowNum = rowNum;
    }
    /**
     * 设置item的背景透明度 范围：0~255
     * @param alpha 取值0~255  0为全透明
     */
    public void setBackgroundAlpha(int alpha){
        mAlpha = alpha;
    }

    public void addBarrageItemView(Context context,String text,int textSize,int textColor){
        createBarrageItemView(context,text,textSize,textColor);
    }
    public void addBarrageItemView(Context context,String text){
        createBarrageItemView(context,text,0,0);
    }
    private void createBarrageItemView(Context context,String text,int textSize,int textColor){
//        if (getChildCount() >= mRowNum){
//            return;
//        }
        final TextView textView = new TextView(context);
        textView.setBackgroundResource(R.drawable.barrage_item_bg);
        if (textColor != 0){
            textView.setTextColor(getResources().getColor(textColor));
        }
        if (textSize != 0){
            textView.setTextSize(textSize);
        }
        textView.setText(text);
        int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
        textView.measure(w, h);
        int height =textView.getMeasuredHeight();
        int width = textView.getMeasuredWidth();
        int row = new Random().nextInt(100) % mRowNum;
        while (needResetRow(row)){

            row = new Random().nextInt(100) % mRowNum;
        }
        mRowPosList.add(row);
        RelativeLayout.LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        lp.topMargin = row * (height + DpPxUtils.dpToPx(10));
//        lastRow = row;
        textView.setLayoutParams(lp);
        textView.setPadding(DpPxUtils.dpToPx(5), DpPxUtils.dpToPx(2), DpPxUtils.dpToPx(15), DpPxUtils.dpToPx(2));
        textView.getBackground().setAlpha(mAlpha);
        this.addView(textView);
//        Animation animation = AnimationHelper.createTranslateAnim(context,mScreenWidth,-mScreenWidth);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                removeView(textView);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        textView.startAnimation(animation);
//        mChildView.add(textView);
    }
    public boolean needResetRow(int row){
        int size = mRowPosList.size();
        int sameRowPos =-1;
        for(int i = size; i > 0; i--){
            if (row == mRowPosList.get(i-1)){
                sameRowPos = i-1;
                break;
            }
        }
        if (sameRowPos != -1){
            TextView tv = mChildView.get(sameRowPos);
            if (mScreenWidth -tv.getX() < tv.getWidth()){
                return true;
            }
        }
        return false;
    }
}