package com.jyx.moveborderline;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private MoveLineFrameLayout moveLineFrameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        moveLineFrameLayout = findViewById(R.id.move_line_layout);
        moveLineFrameLayout.startMove();
    }
}
