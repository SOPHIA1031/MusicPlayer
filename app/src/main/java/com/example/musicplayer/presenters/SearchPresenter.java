package com.example.musicplayer.presenters;

import com.example.musicplayer.api.MusicAPI;
import com.example.musicplayer.interfaces.ISearchCallback;
import com.example.musicplayer.interfaces.ISearchPresenter;
import com.example.musicplayer.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;

import java.util.ArrayList;
import java.util.List;

public class SearchPresenter implements ISearchPresenter {
    private static final String TAG = "SearchPresenter";
    private static final int DEFAULT_PAGE = 1;
    private static SearchPresenter sSearchPresenter = null;
    private MusicAPI mMusicApi;
    private int mCurrentPage = DEFAULT_PAGE;
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private List<ISearchCallback> mCallback = new ArrayList<>();

    private SearchPresenter() {
        mMusicApi = MusicAPI.getInstance();
    }

    public static SearchPresenter getSearchPresenter() {
        if (sSearchPresenter == null) {
            synchronized (SearchPresenter.class) {
                if (sSearchPresenter == null) {
                    sSearchPresenter = new SearchPresenter();
                }
            }
        }
        return sSearchPresenter;
    }

    @Override
    public void doSearch(String keyword) {
        //用于重新搜索
        //当网络不好时，用户会点击重新搜索
        this.mCurrentKeyword = keyword;
        search(keyword);

    }

    private void search(String keyword) {
        mMusicApi.searchByKeyword(keyword, mCurrentPage, new IDataCallBack<SearchAlbumList>() {
            @Override
            public void onSuccess(SearchAlbumList searchAlbumList) {
                List<Album> albums = searchAlbumList.getAlbums();
                if (albums != null) {
                    LogUtil.d(TAG, "albums size -- > " + albums.size());
                } else {
                    LogUtil.d(TAG, "albums is null..");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getHotWord() {
        mMusicApi.getHotWords(new IDataCallBack<HotWordList>() {
            @Override
            public void onSuccess(HotWordList hotWordList) {
                if (hotWordList != null) {
                    List<HotWord> hotWords = hotWordList.getHotWordList();
                    LogUtil.d(TAG, "hotWords -- > " + hotWords.size());
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onHotWordLoaded(hotWords);
                    }
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "getHotWord errorCode -- > " + errorCode);
                LogUtil.d(TAG, "getHotWord errorMsg -- > " + errorMsg);
            }
        });

    }

    @Override
    public void getRecommendWord(String keyword) {
mMusicApi.getSuggestWord(keyword, new IDataCallBack<SuggestWords>() {
    @Override
    public void onSuccess(SuggestWords suggestWords) {
        if (suggestWords != null) {
            List<QueryResult> keyWordList=suggestWords.getKeyWordList();
            LogUtil.d(TAG,"keyWordList -- > "+keyWordList.size());

        }
    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        LogUtil.d(TAG, "getRecommendWord errorCode -- > " + errorCode);
        LogUtil.d(TAG, "getRecommendWord errorMsg -- > " + errorMsg);
    }
});
    }

    @Override
    public void registerViewCallback(ISearchCallback iSearchCallback) {
        if (!mCallback.contains(iSearchCallback)) {
            mCallback.add(iSearchCallback);

        }
    }

    @Override
    public void unRegisterViewCallback(ISearchCallback iSearchCallback) {
        mCallback.remove(iSearchCallback);
    }
}
