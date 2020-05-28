package com.example.musicplayer.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallBack {
    //详情加载出来
    void onDetailListLoaded(List<Track> tracks);
    //把ALBUM传给UI
    void onAlbumLoaded(Album album);
    //网络错误
    void onNetError(int errCode,String errMsg);
    // 加载更多的结果
    void onLoadMoreFinish(int size);
    // 加载刷新结果
    void onRefreshFinish(int size);
}
