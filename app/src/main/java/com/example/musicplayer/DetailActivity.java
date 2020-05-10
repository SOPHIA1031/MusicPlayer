package com.example.musicplayer;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.musicplayer.base.BaseActivity;
import com.example.musicplayer.interfaces.IAlbumDetailPresenter;
import com.example.musicplayer.interfaces.IAlbumDetailViewCallBack;
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallBack {
    private ImageView mLargePic;
    private ImageView mSmallPic;
    private TextView  mAlDetailTitle;
    private TextView mAlDetailAuthor;
    private AlbumDetailPresenter mAlbumDetailPresenter;
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
    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        //加载
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
            Picasso.with(this).load(album.getCoverUrlLarge()).into(mLargePic);
        }
        if (mSmallPic!=null){
            //加载图片
            Picasso.with(this).load(album.getCoverUrlSmall()).into(mSmallPic);
        }
    }
}
