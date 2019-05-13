package com.jyx.pickerview;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 单字符串的pickerView
 *
 * @author Jingyongxuan
 * @date 2019/5/13
 */
public class SimplePickerView extends BasePickerView {

    private PickerView pickerView;

    private String selectedContent;

    public SimplePickerView(Context context) {
        super(context);
    }

    @Override
    public void initPickerView(Context context, LinearLayout target) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        pickerView = new PickerView(context);
        pickerView.setLayoutParams(params);
        target.addView(pickerView);
        pickerView.setOnSelectListener(new PickerView.onSelectListener() {
            @Override
            public void onSelect(Pickers pickers) {
                selectedContent = pickers.getShowContent();
            }
        });
    }

    public void setData(List<Pickers> dataList){
        selectedContent = dataList.get(0).getShowContent();
        pickerView.setData(dataList);
        pickerView.setSelected(0);
    }

    public void setData(String[] data){
        List<Pickers> list = new ArrayList<>();
        for (int i = 0; i < data.length; i++) {
            list.add(new Pickers(data[i], String.valueOf(i)));
        }
        selectedContent = data[0];
        pickerView.setData(list);
        pickerView.setSelected(0);
    }

    public String getSelectedContent(){
        return selectedContent;
    }



}
