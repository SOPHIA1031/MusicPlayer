package com.example.musicplayer;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.musicplayer.adapters.DetailListAdapter;
import com.example.musicplayer.base.BaseActivity;
import com.example.musicplayer.interfaces.IAlbumDetailPresenter;
import com.example.musicplayer.interfaces.IAlbumDetailViewCallBack;
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack, DetailListAdapter.ItemClickListener {
    private ImageView mLargePic;
    private ImageView mSmallPic;
    private TextView  mAlDetailTitle;
    private TextView mAlDetailAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
    private RecyclerView mRecyclerView;
    private DetailListAdapter mDetailListAdapter;
    private int mCurrentPage=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        //getWindow().setStatusBarColor(Color.TRANSPARENT); //顶部透明
        //getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        initView(); //初始化视图
        mAlbumDetailPresenter=AlbumDetailPresenter.getInstance(); //拿到presenter,然后设置回调
        mAlbumDetailPresenter.registerCallBack(this);

    }

    private void initView() {
        mLargePic =this.findViewById(R.id.bg_big_cover); //详情页背景的大图
        mSmallPic =this.findViewById(R.id.bg_small_cover); //专辑图小图
        mAlDetailTitle=this.findViewById(R.id.al_detail_title); //专辑图旁边的专辑标题
        mAlDetailAuthor=this.findViewById(R.id.al_detail_author); //专辑图标题下面的作者
        mRecyclerView=this.findViewById(R.id.album_detail_list);
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

    }

    @Override
    public void onItemClick(){
        //跳转到播放页
        Intent intent=new Intent(this,PlayerActivity.class);
        startActivity(intent);

    }
    @Override
    public void onDetailListLoaded(List<Track> tracks) {
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
            Glide.with(this)
                    .load(album.getCoverUrlLarge()).bitmapTransform(new BlurTransformation(this, 25,1)).into(mLargePic);
                    // 25：模糊半径，越大图片越模糊 范围：1-25，1：缩放倍数
            //Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargePic);
        }
        if (mSmallPic!=null){
            //加载图片
            Picasso.with(this).load(album.getCoverUrlSmall()).into(mSmallPic);
        }

        //获取专辑详情内容
        long id =album.getId();
        mAlbumDetailPresenter.albumDetail((int)id ,mCurrentPage);
    }
}
