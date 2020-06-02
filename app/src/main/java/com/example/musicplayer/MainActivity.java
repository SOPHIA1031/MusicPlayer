package com.example.musicplayer;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.musicplayer.adapters.IndicatorAdapter;
import com.example.musicplayer.adapters.MainContentAdapter;
import com.example.musicplayer.api.MusicDBHelper;
import com.example.musicplayer.interfaces.IPlayerCallback;
import com.example.musicplayer.presenters.PlayerPresenter;
import com.example.musicplayer.presenters.RecommendPresenter;
import com.example.musicplayer.utils.ImageFactory;
import com.example.musicplayer.utils.LogUtil;
import com.hacknife.carouselbanner.Banner;
import com.hacknife.carouselbanner.CoolCarouselBanner;
import com.hacknife.carouselbanner.interfaces.OnCarouselItemChangeListener;
import com.hacknife.carouselbanner.interfaces.OnCarouselItemClickListener;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
//import com.youth.banner.Banner;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity implements IPlayerCallback {

    private static final String TAG="MainActivity";
    private IndicatorAdapter mIndicatorAdapter;
    private ViewPager mContentPager;
    private MagicIndicator mMagicIndicator;
    private View mSearchBtn;
    private ImageView mTrackCover;
    private TextView mMainHeadTitle;
    private TextView mMainSubTitle;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   //给Activity设置一个layout布局
        initView();
        initEvent();
        //
        initPresenter();

    }

    private void initPresenter() {
        mPlayerPresenter=PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);//通知UI更新
    }

    private  void initEvent(){
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG,"click index is"+index);
                if (mContentPager!=null){
                    mContentPager.setCurrentItem(index,false);   //点击导航条时，跳转到对应的页面（封装的方法）
                }
            }
        });
        //下面的播放控制
        mPlayControl.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean hasPlayList=mPlayerPresenter.hasPlayList();
                if(!hasPlayList){
                    //没有设置过播放列表就播放第一首
                    playFirstRecommend();
                }
                else {
                    if (mPlayerPresenter!=null){
                        if (mPlayerPresenter.isPlay()){
                            mPlayerPresenter.pause();
                        }else {
                            mPlayerPresenter.play();
                        }
                    }
                }

            }
        });
        //推荐页跳转到播放器页面，设置点击事件
        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPlayList=mPlayerPresenter.hasPlayList();
                if(!hasPlayList){
                    //没有设置过播放列表就播放第一首
                    playFirstRecommend();
                }
                startActivity(new Intent(MainActivity.this,PlayerActivity.class));
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    //播放第一个推荐内容
    private void playFirstRecommend() {
        List<Album> currentRecommend=RecommendPresenter.getInstance().getCurrentRecommend();
        if (currentRecommend!=null&&currentRecommend.size()>0){
            Album album=currentRecommend.get(0);
            long albumId=album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    //最上面的导航条
    private void initView(){
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));

        //创建indicator的适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true); //自适应宽度，三栏平分
        commonNavigator.setAdapter(mIndicatorAdapter);
        // 设置要显示的内容
        mContentPager =this.findViewById(R.id.content_pager);
        //创建内容适配器
        FragmentManager supportFragmentManager=getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);
        // 绑定
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);// viewpager有滑动监听，下面的页面滑动上面的也跟着滑动
        // 播放控制相关
        mTrackCover =this.findViewById(R.id.track_cover);  //图片
        mMainHeadTitle=this.findViewById(R.id.main_head_title);
        mMainHeadTitle.setSelected(true);
        mMainSubTitle=this.findViewById(R.id.main_sub_title);
        mPlayControl=this.findViewById(R.id.main_play_control);
        mPlayControlItem=this.findViewById(R.id.play_control);
        //搜索
        mSearchBtn=this.findViewById(R.id.search_btn);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter!=null){
            mPlayerPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onPlayStart() {
        //回调通知，通知状态
       updatePlayControl(true);
    }

    private void updatePlayControl(boolean isPlay){
        if (mPlayControl!=null){
            mPlayControl.setImageResource(isPlay?R.drawable.selector_player_stop:R.drawable.selector_play_control_play);
        }
    }
    @Override
    public void onPlayPause() {
        updatePlayControl(false);
    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);
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

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void inAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track!=null){
            String  trackTitle=track.getTrackTitle();
            String singer= track.getAnnouncer().getNickname();
            String cover=track.getCoverUrlMiddle();
            LogUtil.d(TAG,"trackTitle---->"+trackTitle);
            if (trackTitle!=null){
                mMainHeadTitle.setText(trackTitle);
            }
            LogUtil.d(TAG,"singer---->"+singer);
            if (singer!=null){
                mMainSubTitle.setText(singer);
            }
            LogUtil.d(TAG,"cover---->"+cover);
            Picasso.with(this).load(cover).into(mTrackCover);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}
