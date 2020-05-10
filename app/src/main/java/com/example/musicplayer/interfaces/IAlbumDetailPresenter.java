package com.example.musicplayer.interfaces;

public interface IAlbumDetailPresenter {
    //获取专辑详情
    void albumDetail(int id,int page);
    //下拉刷新
    void pulltoRefresh();
    //上拉加载更多
    void loadMore();

    //注册回调，注册UI通知的接口
    void registerCallBack(IAlbumDetailViewCallBack detailViewCallback);
    //取消UI通知接口
    void unRegisterCallBack(IAlbumDetailViewCallBack detailViewCallback);
}
