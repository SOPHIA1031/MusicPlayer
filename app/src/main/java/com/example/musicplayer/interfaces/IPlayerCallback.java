package com.example.musicplayer.interfaces;

import android.os.Trace;

import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

public interface IPlayerCallback {
//   开始播放
    void onPlayStart();
//   播放暂停
    void onPlayPause();
//    播放停止
    void onPlayStop();
//    播放错误
    void onPlayError();
//    下一首播放
    void nextPlay(Track track);
//    上一首播放
    void onPrePlay(Track track);
//    播放列表数据加载完成，list播放器列表数据
    void onListLoaded(List<Track> list);
//    播放器模式改变
    void onPlayModeChange(XmPlayListControl.PlayMode playMode);
//    进度条改变
    void onProgressChange(int currentProgress, int total);
//    广告加载
    void onAdLoading();
//    广告结束
    void inAdFinished();
//    更新当前节目
    void onTrackUpdate(Track track, int playIndex);

}
