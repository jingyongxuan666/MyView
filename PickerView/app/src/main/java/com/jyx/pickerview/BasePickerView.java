package com.jyx.pickerview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public abstract class BasePickerView {

    private PopupWindow popupWindow;

    private Context mContext;

    private View parent;

    private TextView tvConfirm;

    private TextView tvCancel;
    private OnDismissListener onDismissListener;

    public BasePickerView(Context context) {
        this.mContext = context;

        parent = ((Activity)mContext).findViewById(android.R.id.content);
        final View popView = View.inflate(mContext,R.layout.pop_up_window_picker_view,null);
        LinearLayout linearLayout = popView.findViewById(R.id.linear);

        tvConfirm = popView.findViewById(R.id.tv_confirm);
        tvCancel = popView.findViewById(R.id.tv_cancel);

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        //设置popUpWindow
        popupWindow = new PopupWindow(popView,width,height/3);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);

        //隐藏监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowAlpha(false);
                if (onDismissListener != null){
                    onDismissListener.onDismiss();
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        initPickerView(mContext,linearLayout);

    }

    public abstract void initPickerView(Context context,LinearLayout target);


    /**
     * @param isOpen
     * 设置activity透明度
     */
    public void setWindowAlpha(boolean isOpen) {
        final Window window = ((Activity)mContext).getWindow();
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
        setWindowAlpha(true);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,0);
    }

    public void setOnConfirmClickListener(View.OnClickListener clickListener){
        tvConfirm.setOnClickListener(clickListener);
    }

    public void setOnDismissListener(OnDismissListener onDismissListener){
        this.onDismissListener = onDismissListener;
    }

    public void setOnDismissListener(View.OnClickListener clickListener){
        tvCancel.setOnClickListener(clickListener);
    }

    public interface OnDismissListener{
        void onDismiss();
    }


}
