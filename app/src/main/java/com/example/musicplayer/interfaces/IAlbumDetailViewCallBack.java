package com.example.musicplayer.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IAlbumDetailViewCallBack {
    //详情加载出来
    void onDetailListLoaded(List<Track> tracks);
}
