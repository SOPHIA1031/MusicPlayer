package com.example.musicplayer.presenters;

import com.example.musicplayer.interfaces.IAlbumDetailPresenter;
import com.example.musicplayer.interfaces.IAlbumDetailViewCallBack;
import com.example.musicplayer.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private List<IAlbumDetailViewCallBack> mCallbacks=new ArrayList<>();
    private Album mTargetAlbum=null;
    private String TAG="ALBUM DETAIL PRESENTER:";

    //单例模式
    private AlbumDetailPresenter(){
    }
    private static AlbumDetailPresenter sInstance =null;
    //获取单例对象，懒汉式
    public static AlbumDetailPresenter getInstance(){
        if (sInstance==null){
            synchronized (AlbumDetailPresenter.class){
                if (sInstance==null){
                    sInstance=new AlbumDetailPresenter();
                }
            }
        }
        return sInstance;
    }
    @Override
    public void albumDetail(int id, int page) {
        // SDK文档接口3.2.4
        //根据页码和id获取数据
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, id+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, page+"");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>(){
            @Override
            public void onSuccess(TrackList trackList) {
                //只能在主线程中更新UI
                if(trackList!=null) {
                    List<Track> tracks =trackList.getTracks();
                    LogUtil.d(TAG,"track size");
                    handlerAlbumDetailResult(tracks);

                }
            }

            @Override
            public void onError(int errCode, String msg) {
                LogUtil.d(TAG,"errorCode--"+errCode);
                LogUtil.d(TAG,"errorMsg--"+msg);
            }
        });
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallBack mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);//将内容放到UI中
        }
    }

    @Override
    public void pulltoRefresh() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerCallBack(IAlbumDetailViewCallBack detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)){
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum!=null){
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }

        }
    }

    @Override
    public void unRegisterCallBack(IAlbumDetailViewCallBack detailViewCallback) {
        mCallbacks.remove(detailViewCallback);
    }

    public void setTargetAlbum(Album album){
        //加载专辑
        this.mTargetAlbum=album;
    }
}
