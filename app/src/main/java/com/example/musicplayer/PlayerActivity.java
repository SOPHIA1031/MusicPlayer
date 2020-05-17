package com.example.musicplayer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.example.musicplayer.adapters.PlayerTrackPagerAdapter;
import com.example.musicplayer.base.BaseActivity;
import com.example.musicplayer.interfaces.IPlayerCallback;
import com.example.musicplayer.presenters.PlayerPresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

public class PlayerActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {

    private static final String TAG ="PlayerActivity";
    private ImageView mControlBtn;
    private PlayerPresenter mPlayerPresenter;
    private SimpleDateFormat mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat mHourFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mTotalDuration;
    private TextView mCurrentPosition;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchProgressBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitleTv;
    private String mTrackTitleText;
    private ViewPager mTrackPageView;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private boolean mIsUserSlidePager = false;
    private ImageView mPlayModeSwitchBtn;

    private XmPlayListControl.PlayMode mCurrentMode = XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
    private static Map<XmPlayListControl.PlayMode,XmPlayListControl.PlayMode> splayModeRule = new HashMap<>();
    //处理播放模式的切换
    //1.默认模式： PLAY_MODEL_LIST
    //2.列表循环： PLAY_MODEL_LIST_LOOP
    //3.随机播放： PLAY_MODEL_RANDOM
    //4.单曲循环： PLAY_MODEL_SINGLE_LOOP
    static {
        splayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        splayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        splayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        splayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //测试播放
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        initView();
        //在界面初始化之后，再去获取数据
        mPlayerPresenter.getPlayList();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放资源
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }

    //给控件设置相关的事件
    @SuppressLint("ClickableViewAccessibility")
    private void initEvent(){
        mControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果现在的状态是正在播放的那么就暂停
                //todo;
                if (mPlayerPresenter.isPlay()) {
                    mPlayerPresenter.pause();
                }else {
                    //如果现在的状态是非播放的那么就播放
                    mPlayerPresenter.play();
                }
            }
        });
        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                if (isFromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchProgressBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //手离开拖动进度条的时候更新进度
                mIsUserTouchProgressBar = false;
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });
        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:播放上一个节目
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });
        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:播放下一个节目
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });
        mTrackPageView.addOnPageChangeListener(this);
        mTrackPageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }

                return false;
            }
        });

        mPlayModeSwitchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //处理播放模式的切换
                //1.默认模式： PLAY_MODEL_LIST
                //2.列表循环： PLAY_MODEL_LIST_LOOP
                //3.随机播放： PLAY_MODEL_RANDOM
                //4.单曲循环： PLAY_MODEL_SINGLE_LOOP
                //根据当前的mode获取到下一个mode
                XmPlayListControl.PlayMode playMode = splayModeRule.get(mCurrentMode);
                //修改播放模式
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.switchPlayMode(playMode);
                    mCurrentMode = playMode;
                    updatePlayModeBtnImg();
                }
            }
        });
    }
//根据当前的状态模式更新图标
    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_play_mode_list_order;
        switch (mCurrentMode){
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_play_mode_list_order_looper;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_play_mode_list_order_random;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_play_mode_list_order_single_loop;
                break;


        }
        mPlayModeSwitchBtn.setImageResource(resId);
    }

    //找到各个控件
    private void initView(){
        mControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mTotalDuration = this.findViewById(R.id.track_duration);
        mCurrentPosition = this.findViewById(R.id.current_position);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mTrackTitleTv = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitleTv.setText(mTrackTitleText);
        }
        mTrackPageView = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter() ;
        //设置适配器
        mTrackPageView.setAdapter(mTrackPagerAdapter);
        //切换播放模式得按钮
        mPlayModeSwitchBtn = this.findViewById(R.id.player_mode_switch_btn);
    }

    @Override
    public void onPlayStart() {
        //开始播放，修改ui成暂停的按钮
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_stop);
        }
    }

    @Override
    public void onPlayPause() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (mControlBtn != null) {
            mControlBtn.setImageResource(R.drawable.selector_player_play);
        }
    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
        //把数据设置到适配器里
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentDuration, int total) {
        mDurationBar.setMax(total);
        //更新播放进度，更新进度条
        String totalDuration;
        String currentPosition;
        if(total > 1000*60*60){
            totalDuration = mHourFormat.format(total);
            currentPosition = mHourFormat.format(currentDuration);
        }else{
            totalDuration = mMinFormat.format(total);
            currentPosition = mMinFormat.format(currentDuration);
        }
        if (mTotalDuration != null) {
            mTotalDuration.setText(totalDuration);
        }
        //更新当前时间
        if (mCurrentPosition != null) {
            mCurrentPosition.setText(currentPosition);
        }
        //更新进度
        //计算当前进度即百分比
        if (!mIsUserTouchProgressBar) {
            mDurationBar.setProgress(currentDuration);
        }
//        LogUtil.d(TAG,"percent-->" + percent);
    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void inAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        this.mTrackTitleText = track.getTrackTitle();
        if (mTrackTitleTv != null) {
            //设置当前节目的标题
            mTrackTitleTv.setText(mTrackTitleText);
        }
        //当节目改变的时候，我们就获取到当前播放器中播放位置
        //当前的节目改变之后修改页面图片
        if (mTrackPageView != null) {
            //设置为true时，图片切换是平滑的动画
            mTrackPageView.setCurrentItem(playIndex,true);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
    //当页面选中的时候，就去切换播放的内容
        if (mPlayerPresenter != null && mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager = false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
