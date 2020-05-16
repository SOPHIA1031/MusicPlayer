package com.example.musicplayer.interfaces;

import com.example.musicplayer.base.IBasePresenter;

public interface IAlbumDetailPresenter extends IBasePresenter<IAlbumDetailViewCallBack> {
    //获取专辑详情
    void albumDetail(int id,int page);
    //下拉刷新
    void pulltoRefresh();
    //上拉加载更多
    void loadMore();
}
