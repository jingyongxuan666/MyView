package com.jyx.pickerview;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.List;

public class BottomPickerView {

    private PopupWindow popupWindow;

    private Activity mActivity;

    private View parent;

    public BottomPickerView(Activity activity) {
        this.mActivity = activity;

        parent = mActivity.findViewById(android.R.id.content);
        View popView = View.inflate(mActivity,R.layout.pop_up_window_picker_view,null);
        LinearLayout linearLayout = popView.findViewById(R.id.linear);

        int width = mActivity.getResources().getDisplayMetrics().widthPixels;
        int height = mActivity.getResources().getDisplayMetrics().heightPixels;
        popupWindow = new PopupWindow(popView,width,height/3);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x30000000));
        popupWindow.update();

        PickerView pickerView = new PickerView(mActivity);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        pickerView.setLayoutParams(params);
        linearLayout.addView(pickerView);

        final PickerView p2 = new PickerView(mActivity);
        p2.setLayoutParams(params);
        linearLayout.addView(p2);

        final List<Pickers> list = new ArrayList();
        String[] id = new String[] { "1", "2", "3", "4", "5", "6" };
        String[]name = new String[] { "中国银行", "农业银行", "招商银行", "工商银行", "建设银行", "民生银行" };
        String[]name2 = new String[] { "haha", "huhu", "xixi", "hehe", "hah", "oo" };
        for (int i = 0; i < name.length; i++) {
            list.add(new Pickers(name2[i], id[i]));
        }

        List<Pickers> list1 = new ArrayList<>();
        for (int i = 0; i < name.length; i++) {
            list1.add(new Pickers(name[i],id[i],list));
        }

        // 设置数据，默认选择第一条
        pickerView.setData(list1);
        p2.setData(list1.get(0).getPickerList());
        p2.setSelected(0);
        pickerView.setSelected(0);

        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(Pickers pickers) {
                p2.setData(pickers.getPickerList());
                p2.setSelected(0);
            }
        });

        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowAlpa(false);
            }
        });


    }

    public void setWindowAlpa(boolean isOpen) {
        final Window window = mActivity.getWindow();
        final WindowManager.LayoutParams lp = window.getAttributes();
        window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        ValueAnimator animator;
        if (isOpen) {
            animator = ValueAnimator.ofFloat(1.0f, 0.5f);
        } else {
            animator = ValueAnimator.ofFloat(0.5f, 1.0f);
        }
        animator.setDuration(400);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                lp.alpha = alpha;
                window.setAttributes(lp);
            }
        });
        animator.start();
    }

    public void showPickerView(){
        setWindowAlpa(true);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
    }
}
