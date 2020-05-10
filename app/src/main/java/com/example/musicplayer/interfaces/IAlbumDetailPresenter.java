package com.example.musicplayer.interfaces;

public interface IAlbumDetailPresenter {
    //获取专辑详情
    void albumDetail(int id,int page);
    //下拉刷新
    void pulltoRefresh();
    //上拉加载更多
    void loadMore();
}
