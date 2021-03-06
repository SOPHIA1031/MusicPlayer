package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.musicplayer.adapters.DetailListAdapter;
import com.example.musicplayer.base.BaseActivity;
import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.interfaces.IAlbumDetailViewCallBack;
import com.example.musicplayer.interfaces.IPlayerCallback;
import com.example.musicplayer.interfaces.ISubscriptionCallback;
import com.example.musicplayer.interfaces.ISubscriptionPresenter;
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.example.musicplayer.presenters.PlayerPresenter;
import com.example.musicplayer.presenters.SubscriptionPresenter;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.GlideBlurformation;
import com.example.musicplayer.views.UIloader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack, DetailListAdapter.ItemClickListener, IPlayerCallback, ISubscriptionCallback {
    private final static int DEFAULT_PLAY_INDEX = 0;
    TwinklingRefreshLayout mRefreshLayout;
    private ImageView mLargePic;
    private ImageView mSmallPic;
    private TextView mAlDetailTitle;
    private TextView mAlDetailAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private RecyclerView mRecyclerView;
    private DetailListAdapter mDetailListAdapter;
    private int mCurrentPage = 1;
    private FrameLayout mDetailListContainer;
    private UIloader uiLoader;
    private List<Track> mCurrentTrack = null;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private PlayerPresenter mPlayerPresenter;
    private TextView mSubBtn;
    private ISubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;
    private boolean mIsLoaderMore = false;
    private String trackTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getWindow().setStatusBarColor(Color.TRANSPARENT); //顶部透明
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        initView(); //初始化视图
        initPresenter();
        //设置订阅按钮的状态
        updateSubState();

        //判断当前是否为暂停状态，方便图标和文字的修改
        updatePlaySate(mPlayerPresenter.isPlay());
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        //这是专辑详情的presenter
        mAlbumDetailPresenter = AlbumDetailPresenter.getInstance(); //拿到presenter,然后设置回调
        mAlbumDetailPresenter.registerViewCallback(this);

        //这是播放器的Presenter
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);
        //订阅相关的presenter.
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unRegisterViewCallback(this);
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unRegisterViewCallback(this);
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
    }

    private void initListener() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //判断播放器是否有播放列表
                    //todo:
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has) {
                        //控制播放器的状态
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }
                }
            }
        });
        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    //如果没有订阅，就去订阅，如果已经订阅了，那么就取消订阅
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    } else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }

    //播放器里没有播放内容的时候，点击开始播放的话就从列表的第一首歌开始播放
    private void handleNoPlayList() {
        mPlayerPresenter.setsPlayList(mCurrentTrack, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlay()) {
            //正在播放，那么就暂停
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        mDetailListContainer = this.findViewById(R.id.detail_list_container);  //FragmentLayout可以显示任意内容
        if (uiLoader == null) {
            //创建UILOADER
            uiLoader = new UIloader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews(); //添加前去掉所有的
            mDetailListContainer.addView(uiLoader); //新的添加到container里面
        }
        mLargePic = this.findViewById(R.id.bg_big_cover); //详情页背景的大图
        mSmallPic = this.findViewById(R.id.bg_small_cover); //专辑图小图
        mAlDetailTitle = this.findViewById(R.id.al_detail_title); //专辑图旁边的专辑标题
        mAlDetailAuthor = this.findViewById(R.id.al_detail_author); //专辑图标题下面的作者

        //播放控制的图标
        mPlayControlBtn = this.findViewById(R.id.detail_play_icon);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);
        mSubBtn = this.findViewById(R.id.detail_sub_btn);

    }

    private View createSuccessView(ViewGroup container) {
        View mDetailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container, false);
        mRecyclerView = mDetailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = mDetailListView.findViewById(R.id.refresh_layout);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //设置适配器
        mDetailListAdapter = new DetailListAdapter();
        mRecyclerView.setAdapter(mDetailListAdapter);
        //设置item的上下间距
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = 3;
                outRect.bottom = 3;
            }
        });
        mDetailListAdapter.setItemClickListener(this);

        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功", LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                // presenter层去加载更多的内容
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    mIsLoaderMore = true;
                }
            }
        }); //下拉刷新的监听
        return mDetailListView;
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
//        设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setsPlayList(detailData, position);
//        跳转到播放页
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);

    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;

        }
        this.mCurrentTrack = tracks;
        // 判断结果，显示对应UI
        if (tracks == null || tracks.size() == 0) {
            if (uiLoader != null) {
                uiLoader.updateStatus(UIloader.UIStatus.EMPTY);
            }
        }

        if (uiLoader != null) {
            uiLoader.updateStatus(UIloader.UIStatus.SUCCESS);
        }

        //更新/设置UI数据
        mDetailListAdapter.setData(tracks);

    }

    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum = album;
        if (mAlDetailTitle != null) {
            mAlDetailTitle.setText(album.getAlbumTitle());
        }
        if (mAlDetailAuthor != null) {
            mAlDetailAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (mLargePic != null) {
            //做高斯模糊处理
//            Glide.with(this)
//                    .load(album.getCoverUrlLarge()).apply(bitmapTransform(new BlurTransformation(this,25,8))).into(mLargePic);
            // 25：模糊半径，越大图片越模糊 范围：1-25，1：缩放倍数
            //Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargePic);
            Glide.with(this)
                    .load(album.getCoverUrlLarge())
                    .apply(RequestOptions.bitmapTransform(new GlideBlurformation(this)))
                    .into(mLargePic);

        }
        if (mSmallPic != null) {
            //加载图片
            Picasso.with(this).load(album.getCoverUrlSmall()).into(mSmallPic);
        }

        //获取专辑详情内容
        long id = album.getId();
        mAlbumDetailPresenter.albumDetail((int) id, mCurrentPage);
        //拿数据显示loading
        if (uiLoader != null) {
            uiLoader.updateStatus(UIloader.UIStatus.LOADING);
        }
    }

    @Override
    public void onNetError(int errCode, String errMsg) {
        //请求发送错误
        uiLoader.updateStatus(UIloader.UIStatus.NETWORK_ERROR);
    }

    //界面层实现接口里的方法
    @Override
    public void onLoadMoreFinish(int size) {
        if (size > 0) {
            Toast.makeText(this, "成功加载" + size + "条", LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多了", LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinish(int size) {

    }

    private void updatePlaySate(boolean play) {
        if (mPlayControlBtn != null && mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(play ? R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
            if (!play) {
                mPlayControlTips.setText(R.string.pause_tips_text); //显示“已暂停”
            } else {
                if (!TextUtils.isEmpty(trackTitle))
                    mPlayControlTips.setText(trackTitle);
            }

            //mPlayControlTips.setText(play ? R.string.playing_tips_text : R.string.pause_tips_text);
        }
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停，文字为正在播放
        updatePlaySate(true);
    }

    @Override
    public void onPlayPause() {
        //修改图标为播放，文字为已经暂停
        updatePlaySate(false);
    }

    @Override
    public void onPlayStop() {
        //修改图标为播放，文字为已经暂停
        updatePlaySate(false);
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
        //更新播放那里的歌曲标题
        if (track != null) {
            trackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(trackTitle) && mPlayControlTips != null) {
                mPlayControlTips.setText(trackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，就修改UI成取消
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, tipsText, LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，就修改UI成取消
            mSubBtn.setText(R.string.sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this, tipsText, LENGTH_SHORT).show();
    }

    @Override
    public void onSubListLoaded(List<Album> result) {
        //在这个界面不需要处理
    }

    @Override
    public void onSubFull() {
        //处理一个即可
        Toast.makeText(this, "订阅数量不能超过"+ Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }
}


