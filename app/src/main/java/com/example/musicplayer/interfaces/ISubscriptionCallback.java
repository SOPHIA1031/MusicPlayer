package com.example.musicplayer.interfaces;

import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

public interface ISubscriptionCallback {
    /**
     * 调用添加的时候，去通知UI结果
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);

    /**
     * 删除订阅的回调方法
     * @param isSuccess
     */
    void onDeleteResult(boolean isSuccess);

    /**
     * 加载的结果
     * @param result
     */
    void onSubListLoaded(List<Album> result);

    /**
     * 订阅数量满了
     */
    void onSubFull();
}
