package com.jyx.barrageview;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BarrageView barrageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barrageView = findViewById(R.id.my_bar);
        final List<String> list = new ArrayList<>();
        list.add("1.哈哈哈");
        list.add("2.很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长很长的弹幕");
        list.add("3.在座的都是垃圾");
        list.add("4.gkd");
        list.add("5.嚯嚯嚯");
        list.add("6.不错不错");
        list.add("7.发弹幕");
        list.add("8.嘿嘿嘿嘿");
        list.add("9.6666666");
        list.add("10.关注了关注了");
        list.add("11.很到位");

        MyAdapter adapter = new MyAdapter(this);
        adapter.setData(list);
        barrageView.setAdapter(adapter);
        barrageView.allowRepeat(true);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                barrageView.startBarrage();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        barrageView.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        barrageView.startBarrage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
