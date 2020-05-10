package com.example.musicplayer.presenters;

import com.example.musicplayer.interfaces.IAlbumDetailPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

public class AlbumDetailPresenter implements IAlbumDetailPresenter {

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

    public void setTargetAlbum(Album album){
        this.mTargetAlbum=album;
    }
}
