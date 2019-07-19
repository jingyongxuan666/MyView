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
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyBarrageView extends LinearLayout {

    private static final int MAX_ROW = 3;
    private List<RelativeLayout> linearLayouts;
    private static final long INTERVAL = 500;
    private List<String> barrages;
    private int currentIndex;
    private Set<Integer> existLine;

    public MyBarrageView(Context context) {
        super(context);
        init();
    }

    public MyBarrageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyBarrageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linearLayouts = new ArrayList<>();
        barrages = new ArrayList<>();
        existLine = new HashSet<>();
        currentIndex = 0;
        setOrientation(VERTICAL);
    }

    public void setList(final List<String> list) {
        post(new Runnable() {
            @Override
            public void run() {
                int height = getHeight();
                for (int i = 0; i < MAX_ROW; i++) {
                    RelativeLayout relativeLayout = new RelativeLayout(getContext());
                    relativeLayout.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height / MAX_ROW));
                    linearLayouts.add(relativeLayout);
                    addView(relativeLayout);
                }
                if (!list.isEmpty()){
                    barrages.clear();
                    barrages.addAll(list);
                    handler.sendEmptyMessageDelayed(0,INTERVAL);
                }
            }
        });

    }
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            checkBarrages();
            handler.sendEmptyMessageDelayed(0,INTERVAL);
        }
    };

    private void checkBarrages() {
        if (currentIndex == barrages.size()){
            currentIndex = 0;
        }
        String text = barrages.get(currentIndex);
        showBarrage(text);
        currentIndex ++;

    }

    private void showBarrage(final String text){
        int position = (int) (Math.random()*MAX_ROW);
        final TextView textView = new TextView(getContext());
        float sc = textView.getTextSize();

        textView.setPadding(100,20,100,20);
        textView.setSingleLine();
        textView.setTag(position);
        int textLength = (int) (text.length()*sc);
        ObjectAnimator translationX = AnimationHelper.createTranslateAnim(textView,DpPxUtils.getScreenWidth(),-textLength);
        translationX.start();
        translationX.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int p = (int) textView.getTag();
                linearLayouts.get(p).removeView(textView);
                existLine.remove(p);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        textView.setText(text);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),text,Toast.LENGTH_SHORT).show();
            }
        });
        linearLayouts.get(position).addView(textView);
        linearLayouts.get(position).setTag(text.length());
    }

    private  int getBarragePosition(){
        while (true){
            int position = (int) (Math.random()*MAX_ROW);
            if (!existLine.contains(position)){
                existLine.add(position);
                Log.d("position",position+"");
                return position;
            }
        }

    }


}
