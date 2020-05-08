package com.example.musicplayer.interfaces;

public interface IRecommendPresenter {
    //逻辑层，根据界面的动作
    //获取推荐内容
    void getRecommendList();

    //下拉刷新
    void pulltoRefresh();

    //上拉加载更多
    void loadMore();

    //用于注册UI的回调
    void registerViewCallback(IRecommendViewCallback callback);

    //取消UI的回调注册
    void unRegisterViewCallback(IRecommendViewCallback callback);
}
