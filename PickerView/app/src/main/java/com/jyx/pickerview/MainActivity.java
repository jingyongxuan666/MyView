package com.jyx.pickerview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private PickerView pickerView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);

//        final CityPickerView cityPickerView = new CityPickerView(this);
        final SimplePickerView pickerView = new SimplePickerView(this);
        String[] content = new String[]{"哈哈","嘿嘿","呼呼","呵呵"};

        pickerView.setData(content);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickerView.showPickerView();
            }
        });

        pickerView.setOnConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,pickerView.getSelectedContent(),Toast.LENGTH_SHORT).show();
            }
        });

//        cityPickerView.setOnConfirmClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this,cityPickerView.getSelectedContent(),Toast.LENGTH_SHORT).show();
//            }
//        });

//        cityPickerView.setOnCancelClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                cityPickerView.hidePickerView();
//            }
//        });


        
    }
}
