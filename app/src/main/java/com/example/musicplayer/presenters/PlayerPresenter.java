package com.example.musicplayer.presenters;

import android.media.session.MediaController;
import android.os.Trace;

import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.interfaces.IPlayerCallback;
import com.example.musicplayer.interfaces.IPlayerPresenter;
import com.example.musicplayer.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;
    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();

    private  PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告物料相关接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
    }
    public static PlayerPresenter sPlayerPresenter;
    public static PlayerPresenter getPlayerPresenter(){
        if(sPlayerPresenter == null){
            synchronized (PlayerPresenter.class){
                if(sPlayerPresenter == null){
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }
        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;
    public void setsPlayList(List<Track> list, int playIndex){
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
        }else{
            LogUtil.d(TAG,  "mPlayerManager is null");
        }
    }

    @Override
    public void play() {
        if(isPlayListSet){
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int progress) {
    //  更新播放器的进度
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlay() {
        //返回当前是否正在播放
       return mPlayerManager.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        if (!mIPlayerCallbacks.contains(iPlayerCallback)) {
            mIPlayerCallbacks.add(iPlayerCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IPlayerCallback iPlayerCallback) {
            mIPlayerCallbacks.remove(iPlayerCallback);
    }
//======= 广告相关的回调方法 start  ===========
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG,"onStartGetAdsInfo...");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG,"onGetAdsInfo..");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG,"onAdsStartBuffering..");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG,"onAdsStopBuffering..");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG,"onStartPlayAds...");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG,"onCompletePlayAds...");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG,"onError what = ＞" + what + "extra =　＞" + "extra");
    }
//======= 广告相关的回调方法 end  ===========

// ============播放器相关回调方法 start ============
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG,"onPlayStart");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG,"onPlayPause");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG,"onPlayStop");
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG,"onSoundPlayComplete");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG,"onSoundPrepared");
    }

    @Override
    public void onSoundSwitch(PlayableModel playableModel, PlayableModel playableModel1) {
        LogUtil.d(TAG,"onSoundSwitch");
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG,"onBufferingStart");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG,"onBufferingStop");
    }

    @Override
    public void onBufferProgress(int progress) {
        LogUtil.d(TAG,"onBufferProgress..."+ progress);
    }

    @Override
    public void onPlayProgress(int currPos, int duration) {
        //单位是ms
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onProgressChange(currPos, duration);
        }
//        LogUtil.d(TAG,"onPlayProgress..." + currPos + "duration -->" + duration);
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG,"onError e --->" + e);
        return false;
    }
// ============播放器相关回调方法 end ============
}
