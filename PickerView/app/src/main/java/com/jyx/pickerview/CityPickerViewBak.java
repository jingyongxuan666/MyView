package com.jyx.pickerview;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class CityPickerViewBak {

    private PopupWindow popupWindow;

    private Context mContext;

    private View parent;

    private String selectedProvince;

    private String selectedMunicipality;

    private TextView tvConfirm;

    private TextView tvCancel;

    public CityPickerViewBak(Context context) {
        this.mContext = context;

        parent = ((Activity)mContext).findViewById(android.R.id.content);
        View popView = View.inflate(mContext,R.layout.pop_up_window_picker_view,null);
        LinearLayout linearLayout = popView.findViewById(R.id.linear);

        tvConfirm = popView.findViewById(R.id.tv_confirm);
        tvCancel = popView.findViewById(R.id.tv_cancel);

        int width = mContext.getResources().getDisplayMetrics().widthPixels;
        int height = mContext.getResources().getDisplayMetrics().heightPixels;

        //设置popUpWindow
        popupWindow = new PopupWindow(popView,width,height/3);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        //省级
        PickerView pickerProvince = new PickerView(mContext);
        pickerProvince.setLayoutParams(params);
        linearLayout.addView(pickerProvince);

        //市级
        final PickerView pickerMunicipality = new PickerView(mContext);
        pickerMunicipality.setLayoutParams(params);
        linearLayout.addView(pickerMunicipality);

        //设置各省市名称的相关数据
        List<Pickers> cityList = CityData.getCityData();

        //省级
        pickerProvince.setData(cityList);
        //市级
        pickerMunicipality.setData(cityList.get(0).getPickerList());
        //默认都选择第一条
        selectedProvince = cityList.get(0).getShowContent();
        selectedMunicipality = cityList.get(0).getPickerList().get(0).getShowContent();
        pickerProvince.setSelected(0);
        pickerMunicipality.setSelected(0);
        //滚动选择监听
        pickerProvince.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(Pickers pickers) {
                selectedProvince = pickers.getShowContent();
                //设置当前滚动到的省级下面的市级数据，默认第一条
                List<Pickers> tempList = new ArrayList<>(pickers.getPickerList());
                selectedMunicipality = tempList.get(0).getShowContent();
                pickerMunicipality.setData(tempList);
                pickerMunicipality.setSelected(0);
            }
        });

        pickerMunicipality.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(Pickers pickers) {
                selectedMunicipality = pickers.getShowContent();
            }
        });

        //隐藏监听
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowAlpha(false);
            }
        });


    }

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

    public String getSelectedContent(){
        return selectedProvince+" "+selectedMunicipality;
    }

    public void setOnConfirmClickListener(View.OnClickListener clickListener){
        tvConfirm.setOnClickListener(clickListener);
    }

    public void setOnCancelClickListener(View.OnClickListener clickListener){
        tvCancel.setOnClickListener(clickListener);
    }

    public void hidePickerView(){
        popupWindow.dismiss();
    }
}
