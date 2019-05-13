package com.jyx.pickerview;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择城市的pickerView.
 *
 * @author Jingyongxuan
 * @date 2019/5/13
 */
public class CityPickerView extends BasePickerView {

    private String selectedProvince;

    private String selectedMunicipality;

    public CityPickerView(Context context) {
        super(context);
    }

    @Override
    public void initPickerView(Context context,LinearLayout target) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1.0f);
        //省级
        PickerView pickerProvince = new PickerView(context);
        pickerProvince.setLayoutParams(params);
        target.addView(pickerProvince);

        //市级
        final PickerView pickerMunicipality = new PickerView(context);
        pickerMunicipality.setLayoutParams(params);
        target.addView(pickerMunicipality);

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
    }

    public String getSelectedContent(){
        return selectedProvince+" "+selectedMunicipality;
    }


}
