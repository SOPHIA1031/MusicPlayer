package com.example.musicplayer.interfaces;

import com.example.musicplayer.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerControl;

public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {
//    播放
    void play();
//    暂停
    void pause();
//    停止播放
    void stop();
//    播放上一首
    void playPre();
//    播放下一首
    void playNext();
//    切换播放模式
    void switchPlayMode(XmPlayListControl.PlayMode mode);
//    获取播放列表
    void getPlayList();
//    根据节目的位置进行播放
//    index是节目再列表中的位置
    void playByIndex(int index);
//    切换播放进度
    void seekTo(int progress);
//    判断播放器是否在播放
    boolean isPlay();

}
