package com.example.musicplayer.interfaces;

import com.example.musicplayer.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {
    /**
     * 获取历史内容
     *
     */
    void listHistory();

    /**
     * 添加历史
     * @param track
     */
     void addHistory(Track track);

    /**
     * 删除历史
     * @param track
     */
     void delHistory(Track track);

    /**
     * 清除历史
     */
     void cleanHistory();




}
