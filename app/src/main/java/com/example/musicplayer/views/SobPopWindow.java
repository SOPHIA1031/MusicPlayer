package com.example.musicplayer.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapters.PlayListAdapter;
import com.example.musicplayer.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private View mCloseBtn;
    private RecyclerView mTrackList;
    private PlayListAdapter mPlayListAdapter;

    public SobPopWindow(){
        //设置它的宽高
        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);

        //载进来view
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置弹窗进入和弹出的动画
        setAnimationStyle(R.style.pop_animation);
        //找到控件
        initView();
        //设置点击事件
        initEvent();
    }

    private void initView() {
        mCloseBtn = mPopView.findViewById(R.id.play_list_close_btn);
        //找到控件
        mTrackList = mPopView.findViewById(R.id.player_list_rv);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mTrackList.setLayoutManager(layoutManager);
        //设置适配器
        mPlayListAdapter = new PlayListAdapter();
        mTrackList.setAdapter(mPlayListAdapter);

    }

    private void initEvent() {
        //点击关闭，弹窗消失
        mCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });
    }
    //给适配器设置数据
    public void setListData(List<Track> data){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(data);
        }
    }

    public void setCurrentPlayPosition(int position){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(position);
            mTrackList.scrollToPosition(position);
        }
    }

    public void setPlayListItemClickListener(PlayListItemClickListener listener){
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    public interface PlayListItemClickListener {
        void onItemClick(int position);
    }
}
