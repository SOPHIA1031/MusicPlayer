package com.example.musicplayer.presenters;

import com.example.musicplayer.api.MusicAPI;
import com.example.musicplayer.interfaces.ISearchCallback;
import com.example.musicplayer.interfaces.ISearchPresenter;
import com.example.musicplayer.utils.Constants;
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
    private List<Album> searchResult = new ArrayList<>();
    private MusicAPI mMusicApi;
    private int mCurrentPage = DEFAULT_PAGE;
    //当前的搜索关键字
    private String mCurrentKeyword = null;
    private List<ISearchCallback> mCallback = new ArrayList<>();
    private boolean mIsLoadMore = false;

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
        mCurrentPage = DEFAULT_PAGE;
        searchResult.clear();
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
                searchResult.addAll(albums);
                if (albums != null) {
                    LogUtil.d(TAG, "albums size -- > " + albums.size());
                    if (mIsLoadMore) {
                        for (ISearchCallback iSearchCallback : mCallback) {
                            iSearchCallback.onLoadMoreResult(searchResult, albums.size() != 0);
                        }
                        mIsLoadMore = false;
                    } else {
                        for (ISearchCallback iSearchCallback : mCallback) {
                            iSearchCallback.onSearchResultLoaded(searchResult);
                        }
                    }
                } else {
                    LogUtil.d(TAG, "albums is null..");
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                LogUtil.d(TAG, "errorCode -- > " + errorCode);
                LogUtil.d(TAG, "errorMsg -- > " + errorMsg);
                for (ISearchCallback iSearchCallback : mCallback) {
                    if (mIsLoadMore) {
                        iSearchCallback.onLoadMoreResult(searchResult, false);
                        mCurrentPage--;
                        mIsLoadMore = false;
                    } else {
                        iSearchCallback.onError(errorCode, errorMsg);
                    }

                }
            }
        });
    }

    @Override
    public void reSearch() {
        search(mCurrentKeyword);
    }

    @Override
    public void loadMore() {
        //判断有没有必要进行加载更多
        if (searchResult.size() < Constants.COUNT_DEFAULT) {
            for (ISearchCallback iSearchCallback : mCallback) {
                iSearchCallback.onLoadMoreResult(searchResult, false);
            }
        } else {
            mIsLoadMore = true;
            mCurrentPage++;
        }
        search(mCurrentKeyword);
    }

    @Override
    public void getHotWord() {
        //todo：做一个热词缓存

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
                    List<QueryResult> keyWordList = suggestWords.getKeyWordList();
                    LogUtil.d(TAG, "keyWordList -- > " + keyWordList.size());
                    for (ISearchCallback iSearchCallback : mCallback) {
                        iSearchCallback.onRecommendWordLoaded(keyWordList);
                    }
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
