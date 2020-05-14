package com.example.musicplayer.api;

import com.example.musicplayer.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.HashMap;
import java.util.Map;

public class MusicAPI {

    private MusicAPI(){ }
    private static MusicAPI sInstance =null;

    //获取单例对象，懒汉型
    public static MusicAPI getInstance(){
        if (sInstance==null){
            synchronized (MusicAPI.class){
                if (sInstance==null){
                    sInstance=new MusicAPI();
                }
            }
        }
        return sInstance;
    }
    //获取推荐内容   callback回调
    //（SDK文档接口3.10.6）
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack){
        Map<String, String> map = new HashMap<>();
        // 一页数据的返回数量
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }

    //根据ID获取详情
    public void getAlbumDetail(IDataCallBack<TrackList> callback,int pageIndex,long albumId){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex+"");
        map.put(DTransferConstants.ALBUM_ID,albumId+"");
        map.put(DTransferConstants.PAGE_SIZE, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getTracks(map,callback);
    }
}
