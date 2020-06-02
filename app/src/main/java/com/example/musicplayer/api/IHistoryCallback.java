package com.example.musicplayer.api;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

public interface IHistoryCallback {
    /**
     * 添加历史的结果
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);

    /**
     * 删除历史的结果
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);

    /**
     * 历史数据加载的结果
     * @param tracks
     */
    void onHistoryLoaded(List<Track> tracks);

    /**
     * 历史数据清除的结果
     */
    void onHistoryClean(boolean isSuccess);
}
