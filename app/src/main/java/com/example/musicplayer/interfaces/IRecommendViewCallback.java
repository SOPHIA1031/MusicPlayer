package com.example.musicplayer.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface IRecommendViewCallback {
    //根据界面的动作调用回调函数
    //获取推荐内容的结果
    void onRecommendListLoaded(List<Album> result);

    //网络错误
    void onNetWorkError();

    //数据为空
    void onDataEmpty();

    //正在加载
    void onLoading();

}
