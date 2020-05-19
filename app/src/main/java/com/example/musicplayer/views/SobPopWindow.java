package com.example.musicplayer.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.musicplayer.R;
import com.example.musicplayer.base.BaseApplication;

public class SobPopWindow extends PopupWindow {
    public SobPopWindow(){
        //设置它的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        //载进来view
        View popView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(popView);
        //设置弹窗进入和弹出的动画
        setAnimationStyle(R.style.pop_animation);
    }
}
