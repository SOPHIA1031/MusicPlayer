package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
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
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.example.musicplayer.presenters.PlayerPresenter;
import com.example.musicplayer.utils.GlideBlurformation;
import com.example.musicplayer.views.UIloader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack, DetailListAdapter.ItemClickListener {
    private ImageView mLargePic;
    private ImageView mSmallPic;
    private TextView  mAlDetailTitle;
    private TextView mAlDetailAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private RecyclerView mRecyclerView;
    private DetailListAdapter mDetailListAdapter;
    private int mCurrentPage=1;
    private FrameLayout mDetailListContainer;
    private UIloader uiLoader;
    TwinklingRefreshLayout mRefreshLayout;
    private List<Track> mCurrentTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getWindow().setStatusBarColor(Color.TRANSPARENT); //顶部透明
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        initView(); //初始化视图
        mAlbumDetailPresenter=AlbumDetailPresenter.getInstance(); //拿到presenter,然后设置回调
        mAlbumDetailPresenter.registerViewCallback(this);

    }

    private void initView() {
        mDetailListContainer=this.findViewById(R.id.detail_list_container);  //FragmentLayout可以显示任意内容
        if (uiLoader==null){
            //创建UILOADER
            uiLoader =new UIloader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews(); //添加前去掉所有的
            mDetailListContainer.addView(uiLoader); //新的添加到container里面
        }
        mLargePic =this.findViewById(R.id.bg_big_cover); //详情页背景的大图
        mSmallPic =this.findViewById(R.id.bg_small_cover); //专辑图小图
        mAlDetailTitle=this.findViewById(R.id.al_detail_title); //专辑图旁边的专辑标题
        mAlDetailAuthor=this.findViewById(R.id.al_detail_author); //专辑图标题下面的作者


    }
    private  boolean mIsLoaderMore=false;
    private View createSuccessView(ViewGroup container){
        View mDetailListView=LayoutInflater.from(this).inflate(R.layout.item_detail_list,container,false);
        mRecyclerView=mDetailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = mDetailListView.findViewById(R.id.refresh_layout);
        //设置布局管理器
        LinearLayoutManager layoutManager =new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        //设置适配器
        mDetailListAdapter=new DetailListAdapter();
        mRecyclerView.setAdapter(mDetailListAdapter);
        //设置item的上下间距
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top=3;
                outRect.bottom=3;
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
                        Toast.makeText(DetailActivity.this,"刷新成功",Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                },2000);
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                // presenter层去加载更多的内容
                if (mAlbumDetailPresenter!=null){
                    mAlbumDetailPresenter.loadMore();
                    mIsLoaderMore=true;
                }
            }
        }); //下拉刷新的监听
        return mDetailListView;
    }

    @Override
    public void onItemClick(List<Track> detailData, int position){
//        设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setsPlayList(detailData, position);
//        跳转到播放页
        Intent intent=new Intent(this,PlayerActivity.class);
        startActivity(intent);

    }
    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        if (mIsLoaderMore&& mRefreshLayout!=null){
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore=false;

        }
        this.mCurrentTrack=tracks;
        // 判断结果，显示对应UI
        if(tracks==null || tracks.size()==0){
            if (uiLoader!=null) {
                uiLoader.updateStatus(UIloader.UIStatus.EMPTY);
            }
        }

        if (uiLoader!=null){
            uiLoader.updateStatus(UIloader.UIStatus.SUCCESS);
        }

        //更新/设置UI数据
        mDetailListAdapter.setData(tracks);

    }

    @Override
    public void onAlbumLoaded(Album album) {
        if (mAlDetailTitle!=null){
            mAlDetailTitle.setText(album.getAlbumTitle());
        }
        if (mAlDetailAuthor!=null){
            mAlDetailAuthor.setText(album.getAnnouncer().getNickname());
        }
        if (mLargePic!=null){
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
        if (mSmallPic!=null){
            //加载图片
            Picasso.with(this).load(album.getCoverUrlSmall()).into(mSmallPic);
        }

        //获取专辑详情内容
        long id =album.getId();
        mAlbumDetailPresenter.albumDetail((int)id ,mCurrentPage);
        //拿数据显示loading
        if (uiLoader!=null){
            uiLoader.updateStatus(UIloader.UIStatus.LOADING);
        }
    }

    @Override
    public void onNetError(int errCode, String errMsg) {
        //请求发送错误
        uiLoader.updateStatus(UIloader.UIStatus.NETWORK_ERROR);
    }
}
