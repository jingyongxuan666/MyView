package com.jyx.barrageview;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BarrageView extends LinearLayout {
    /**
     * 弹幕延迟时间
     */
    private static final long INTERVAL = 500;

    private int MAX_ROW = 3;
    private int barrageIndex;
    private int screenWidth;
    private Adapter adapter;
    private List<RelativeLayout> barrageWrapper;
    private RelativeLayout.LayoutParams relativeParams;
    private boolean allowRepeat;
    private boolean stop;
    private boolean isInit;
    private int height;
    private List<ObjectAnimator> animators;

    public BarrageView(Context context) {
        super(context);
        init();
    }

    public BarrageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarrageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        isInit = true;
        screenWidth = DpPxUtils.getScreenWidth();
        animators = new ArrayList<>();
        //让弹幕在每个轨道都垂直居中
        relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        barrageWrapper = new ArrayList<>();
        barrageIndex = 0;
        setOrientation(VERTICAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed){
            removeAllViews();
            barrageWrapper.clear();
            for (int i = 0; i < MAX_ROW; i++) {
                RelativeLayout relativeLayout = new RelativeLayout(getContext());
                relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height/MAX_ROW));
                barrageWrapper.add(relativeLayout);
                addView(relativeLayout);
            }
        }
    }

    /**
     * 设置适配器
     *
     * @param adapter
     */
    public void setAdapter(Adapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 设置弹幕最大行数
     *
     * @param maxRow
     */
    public void setMaxRow(int maxRow) {
        MAX_ROW = maxRow;
        postInvalidate();
    }

    /**
     * 是否循环显示
     * @param allowRepeat
     */
    public void allowRepeat(boolean allowRepeat){
        this.allowRepeat = allowRepeat;
    }

    public void stop(){
        this.stop = true;
        //移除handler里所有message
        handler.removeCallbacksAndMessages(null);
        //取消所有动画
        if (animators != null){
            for (ObjectAnimator objectAnimator : animators) {
                objectAnimator.cancel();
            }
        }
    }

    /**
     * 弹幕开始滚动
     */
    public void startBarrage() {
        if (!isInit){
            init();
        }
        stop = false;
        for (int i = 0; i < MAX_ROW; i++) {
            handler.sendEmptyMessageDelayed(i, INTERVAL * i);
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            checkBarrages(msg.what);
        }
    };

    /**
     * 用于加载弹幕
     *
     * @param position 弹幕垂直方向位置，上到下，0开始
     */
    private synchronized void checkBarrages(int position) {
        if (stop){
            return;
        }
        if (barrageIndex == adapter.getBarrageCount()) {
            if (allowRepeat){
                barrageIndex = 0;
            }else {
                return;
            }
        }
        showBarrage(position, barrageIndex);
        barrageIndex++;
    }

    /**
     * 显示弹幕
     *
     * @param position 弹幕位置
     * @param index    子view的索引
     */
    private void showBarrage(final int position, int index) {
        Log.d("测试","aaa");
        final ViewHolder holder = (ViewHolder) adapter.onCreateViewHolder(this);
        adapter.onBindViewHolder(holder, index);
        //得到宽度，用来设置动画结束位置
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        holder.itemView.measure(w, h);
        int width = holder.itemView.getMeasuredWidth();
        //设置动画，从屏幕右侧到屏幕左侧-width的地方
        final ObjectAnimator translationX = AnimationHelper.createTranslateAnim(holder.itemView, screenWidth, -width);
        //将弹幕添加到屏幕上
        barrageWrapper.get(position).addView(holder.itemView,relativeParams);
        translationX.start();
        animators.add(translationX);
        translationX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //动画结束就移除这个弹幕的view
                barrageWrapper.get(position).removeView(holder.itemView);
                handler.sendEmptyMessageDelayed(position, INTERVAL);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                barrageWrapper.get(position).removeView(holder.itemView);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    /**
     * item的适配器
     *
     * @param <VH>
     */
    public static abstract class Adapter<VH> {

        public abstract VH onCreateViewHolder(ViewGroup parent);

        public abstract void onBindViewHolder(VH holder, int index);

        public abstract int getBarrageCount();

    }

    public abstract static class ViewHolder {

        public final View itemView;

        protected ViewHolder(@Nullable View itemView) {
            this.itemView = itemView;
        }
    }

}
