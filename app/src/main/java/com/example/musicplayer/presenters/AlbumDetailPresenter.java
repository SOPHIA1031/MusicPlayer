package com.example.musicplayer.presenters;

import com.example.musicplayer.api.MusicAPI;
import com.example.musicplayer.interfaces.IAlbumDetailPresenter;
import com.example.musicplayer.interfaces.IAlbumDetailViewCallBack;
import com.example.musicplayer.utils.Constants;
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
    private List<Track> mTracks =new ArrayList<>();
    private Album mTargetAlbum=null;
    private String TAG="ALBUM DETAIL PRESENTER:";
    private int mCurrentAlbumId=-1;  //当前专辑ID
    private int mCurrentPageIndex=0;  //当前页

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
        //根据Id获取数据
        mTracks.clear();
        this.mCurrentAlbumId=id;
        this.mCurrentPageIndex=page;
        //根据id和page获取专辑
        toDoLoad(false);
    }

    //如果发生错误返回给UI
    private void handlerError(int errCode,String errMsg){
        for (IAlbumDetailViewCallBack mCallback : mCallbacks) {
            mCallback.onNetError(errCode, errMsg); //传回去
        }
    }
    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallBack mCallback : mCallbacks) {
            mCallback.onDetailListLoaded(tracks);//将内容放到UI中
        }
    }

    @Override
    public void pulltoRefresh() {

    }

    //加载更多内容
    @Override
    public void loadMore() {
        mCurrentPageIndex++;
        toDoLoad(true);
    }

    @Override
    public void registerViewCallback(IAlbumDetailViewCallBack detailViewCallback) {
        if (!mCallbacks.contains(detailViewCallback)){
            mCallbacks.add(detailViewCallback);
            if (mTargetAlbum!=null){
                detailViewCallback.onAlbumLoaded(mTargetAlbum);
            }

        }
    }

    @Override
    public void unRegisterViewCallback(IAlbumDetailViewCallBack detailViewCallback) {
        mCallbacks.remove(detailViewCallback);
    }

    public void setTargetAlbum(Album album){
        //加载专辑
        this.mTargetAlbum=album;
    }

    private void toDoLoad(final boolean isMore){
        MusicAPI musicAPI=MusicAPI.getInstance();
        musicAPI.getAlbumDetail(new IDataCallBack<TrackList>(){
            @Override
            public void onSuccess(TrackList trackList) {
                //只能在主线程中更新UI
                if(trackList!=null) {
                    List<Track> tracks =trackList.getTracks();
                    if (isMore){
                        //上拉加载，结果放到前面
                        mTracks.addAll(mTracks.size()-1,tracks);
                    }else {
                        //下拉结果在后面
                        mTracks.addAll(tracks);
                    }

                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errCode, String msg) {
                if (isMore){
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG,"errorCode--"+errCode);
                LogUtil.d(TAG,"errorMsg--"+msg);
                handlerError(errCode,msg);  //错误时显示网络错误
            }
        },mCurrentPageIndex,mCurrentAlbumId);
    }
}
