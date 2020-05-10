package com.example.musicplayer.presenters;

import com.example.musicplayer.interfaces.IAlbumDetailPresenter;
import com.example.musicplayer.interfaces.IAlbumDetailViewCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {
    private List<IAlbumDetailViewCallBack> mCallbacks=new ArrayList<>();
    private Album mTargetAlbum=null;
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
