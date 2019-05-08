package com.jyx.countedittext;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    CountEditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = findViewById(R.id.countEditText);
        text.setOnTextOverLimitedListener(new CountEditText.OnTextOverLimitedListener() {
            @Override
            public void overLimited() {
                Toast.makeText(MainActivity.this,"hahahahaa",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
