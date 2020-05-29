package com.example.musicplayer.presenters;

import androidx.annotation.Nullable;

import com.example.musicplayer.api.MusicAPI;
import com.example.musicplayer.interfaces.IRecommendPresenter;
import com.example.musicplayer.interfaces.IRecommendViewCallback;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendPresenter implements IRecommendPresenter {
    //实现类
    private static final String TAG="RecommendPresenter：";
    private List<IRecommendViewCallback> mCallbacks =new ArrayList<>();

    private RecommendPresenter(){ }
    private static RecommendPresenter sInstance =null;
    private List<Album> mCurrentRecommend=null;

    //获取单例对象，懒汉型
    public static RecommendPresenter getInstance(){
        if (sInstance==null){
            synchronized (RecommendPresenter.class){
                if (sInstance==null){
                    sInstance=new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }

    public List<Album> getCurrentRecommend(){
        return mCurrentRecommend;
    }
    @Override
    public void getRecommendList() {
        updateLoading();
        MusicAPI musicAPI=MusicAPI.getInstance();
        musicAPI.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            //回调函数,手机记得联网
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList) {
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    //upRecommendUI(albumList);
                    // 获取到数据后更新UI
                    handlerRecommendResult(albumList);
                }
            }

            @Override
            public void onError(int i, String s) {
                //报错
                LogUtil.d(TAG, "ERROR" + i + "-->" + s);
                handlerError();
            }
        });
    }

    private void handlerError(){
        if (mCallbacks!=null){
            for (IRecommendViewCallback callback : mCallbacks) {
                callback.onNetWorkError();
            }
        }
    }
    private void handlerRecommendResult(List<Album> albumList){
        //通知UI更新
        if (mCallbacks!=null){
            if (albumList.size()==0){
                //数据为空
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onDataEmpty();
                }
            }else{
                for (IRecommendViewCallback callback : mCallbacks) {
                    callback.onRecommendListLoaded(albumList);
                }
                this.mCurrentRecommend=albumList;
            }

        }
    }

    private void updateLoading(){
        for (IRecommendViewCallback callback : mCallbacks) {
            callback.onLoading();
        }
    }

    @Override
    public void pulltoRefresh() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if(mCallbacks!=null&& !mCallbacks.contains(callback)) //是否包含回调
        {
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unRegisterViewCallback(IRecommendViewCallback callback) {
        if(mCallbacks!=null){
            mCallbacks.remove(callback);
        }
    }
}
