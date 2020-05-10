package com.example.musicplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.musicplayer.base.BaseActivity;

public class DetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getWindow().setStatusBarColor(Color.TRANSPARENT); //顶部透明
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

}
