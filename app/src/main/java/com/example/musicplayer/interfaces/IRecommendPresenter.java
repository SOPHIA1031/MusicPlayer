package com.example.musicplayer.interfaces;

import com.example.musicplayer.base.IBasePresenter;

public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {
    //逻辑层，根据界面的动作
    //获取推荐内容
    void getRecommendList();

    //下拉刷新
    void pulltoRefresh();

    //上拉加载更多
    void loadMore();
}
