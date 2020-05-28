package com.example.musicplayer.presenters;

import android.content.Context;
import android.content.SharedPreferences;
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
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {

    private static final String TAG = "PlayerPresenter";
    private final XmPlayerManager mPlayerManager;
    private List<IPlayerCallback> mIPlayerCallbacks = new ArrayList<>();
    private Track mCurrentTrack;
    private int mCurrentIndex = 0;
    private final SharedPreferences mPlayModSp;
    private XmPlayListControl.PlayMode mCurrentPlayMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    private boolean misReverse = false;

//    PLAY_MODEL_LIST
//    PLAY_MODEL_LIST_LOOP
//    PLAY_MODEL_RANDOM
//    PLAY_MODEL_SINGLE_LOOP
    private static final int PLAY_MODE_LIST_INT = 0;
    private static final int PLAY_MODE_LIST_LOOP_INT = 1;
    private static final int PLAY_MODE_RANDOM_INT = 2;
    private static final int PLAY_MODE_SINGLE_LOOP_INT = 3;
//    sp's key and name
    public static final String PLAY_MODE_SP_NAME = "PlayMod";
    public static final String PLAY_MODE_SP_KEY = "currentPlayMode";

    private  PlayerPresenter(){
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告物料相关接口
        mPlayerManager.addAdsStatusListener(this);
        //注册播放器状态相关的接口
        mPlayerManager.addPlayerStatusListener(this);
        //需要记录当前播放模式
        mPlayModSp = BaseApplication.getAppContext().getSharedPreferences(PLAY_MODE_SP_NAME, Context.MODE_PRIVATE);
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
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
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
        //播放前一个节目
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        //播放后一个节目
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }
    }

    public boolean hasPlayList(){
        //判断当前是否有节目列表
        return isPlayListSet;
    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayMode(mode);
            mCurrentPlayMode = mode;
            //通知UI更新播放模式
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onPlayModeChange(mode);
            }
            //保存到Sp里
            SharedPreferences.Editor editor = mPlayModSp.edit();
            editor.putInt(PLAY_MODE_SP_KEY,getIntByPlayMode(mode));
            editor.commit();
        }
    }

    private int getIntByPlayMode(XmPlayListControl.PlayMode mode){
        switch (mode) {
            case PLAY_MODEL_SINGLE_LOOP:
                return PLAY_MODE_SINGLE_LOOP_INT;
            case PLAY_MODEL_LIST_LOOP:
                return PLAY_MODE_LIST_LOOP_INT;
            case PLAY_MODEL_RANDOM:
                return PLAY_MODE_RANDOM_INT;
            case PLAY_MODEL_LIST:
                return PLAY_MODE_LIST_INT;
        }
        return PLAY_MODE_LIST_INT;
    }

    private XmPlayListControl.PlayMode getModeByInt(int index){
        switch (index) {
            case PLAY_MODE_SINGLE_LOOP_INT:
                return PLAY_MODEL_SINGLE_LOOP;
            case PLAY_MODE_LIST_LOOP_INT:
                return PLAY_MODEL_LIST_LOOP;
            case PLAY_MODE_RANDOM_INT:
                return PLAY_MODEL_RANDOM;
            case PLAY_MODE_LIST_INT:
                return PLAY_MODEL_LIST;
        }
        return PLAY_MODEL_LIST;
    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
    //切换播放器到index得位置进行播放
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }
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
    public void reversePlayList() {
        //播放列表翻转
        List<Track> playList = mPlayerManager.getPlayList();
        Collections.reverse(playList);
        misReverse = !misReverse;
        mCurrentIndex = playList.size()-1-mCurrentIndex;
        mPlayerManager.setPlayList(playList,mCurrentIndex);
        //更新UI
        mCurrentTrack = (Track) mPlayerManager.getCurrSound();
        for (IPlayerCallback iPlayerCallback : mIPlayerCallbacks) {
            iPlayerCallback.onListLoaded(playList);
            iPlayerCallback.onTrackUpdate(mCurrentTrack,mCurrentIndex);
            iPlayerCallback.updateListOrder(misReverse);
        }
    }

    //推荐页默认播放第一张专辑第一首
    @Override
    public void playByAlbumId(long id) {
        //1.获取专辑的列表
        //2.把专辑设置给播放器
        //3.播放
    }


    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
        //从Sp里拿
        int modeIndex = mPlayModSp.getInt(PLAY_MODE_SP_KEY, PLAY_MODE_LIST_INT);
        mCurrentPlayMode = getModeByInt(modeIndex);
        //
        iPlayerCallback.onPlayModeChange(mCurrentPlayMode);
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
        mPlayerManager.setPlayMode(mCurrentPlayMode);
        if (mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备完了，可以播放了
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG,"onSoundSwitch");
        if (lastModel != null) {
            LogUtil.d(TAG,"lastModel..." + lastModel.getKind());
        }
        LogUtil.d(TAG,"curModel.." + curModel.getKind());
        //curModel代表当前播放的内容
        //通过getKind（）来获取它的类型
        //track表示的是Track的类型
        //第一种写法,如果类型是track则强制类型转换为Track
        // if ("track".equals(curModel.getKind())) {
        //     Track currentTrack = (Track) curModel;
        //     LogUtil.d(TAG,"title-->" + currentTrack.getTrackTitle());
        //     }
        //第二种写法
        mCurrentIndex = mPlayerManager.getCurrentIndex();
        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            //LogUtil.d(TAG,"title-->" + currentTrack.getTrackTitle());
            //更新UI
            for (IPlayerCallback iPlayerCallback: mIPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            }
        }
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
