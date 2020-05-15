package com.example.musicplayer.api;

import com.example.musicplayer.utils.Constants;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.HashMap;
import java.util.Map;

public class MusicAPI {

    private static MusicAPI sInstance = null;

    private MusicAPI() {
    }

    //获取单例对象，懒汉型
    public static MusicAPI getInstance() {
        if (sInstance == null) {
            synchronized (MusicAPI.class) {
                if (sInstance == null) {
                    sInstance = new MusicAPI();
                }
            }
        }
        return sInstance;
    }

    //获取推荐内容   callback回调
    //（SDK文档接口3.10.6）
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        // 一页数据的返回数量
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, callBack);
    }

    //根据ID获取详情
    public void getAlbumDetail(IDataCallBack<TrackList> callback, int pageIndex, long albumId) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getTracks(map, callback);
    }

    /**
     * 根据关键字，进行搜索
     *
     * @param keyword
     */
    public void searchByKeyword(String keyword, int page, IDataCallBack<SearchAlbumList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getSearchedAlbums(map, callback);
    }

    /**
     * 获取推荐的热词
     * @param callback
     */
    public void getHotWords(IDataCallBack<HotWordList> callback) {
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.TOP, Constants.COUNT_HOT_WORD + "");
        CommonRequest.getHotWords(map, callback);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword 关键字
     * @param callback 回调
     */
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callback){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map, callback);
    }
}
