package com.example.musicplayer.presenters;

import com.example.musicplayer.api.HistoryDao;
import com.example.musicplayer.api.IHistoryDao;
import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.interfaces.IHistoryCallback;
import com.example.musicplayer.interfaces.IHistoryPresenter;
import com.example.musicplayer.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class HistoryPresenter implements IHistoryPresenter, com.example.musicplayer.api.IHistoryCallback {
    private static HistoryPresenter sHistoryPresenter = null;
    private final IHistoryDao mHistoryDao;
    private List<IHistoryCallback> mCallbacks = new ArrayList<>();
    private List<Track> mCurrentHistories = null;
    private boolean isDoDelOutOfSize = false;
    private Track mCurrentAddTrack=null;

    private HistoryPresenter() {
        mHistoryDao = new HistoryDao();
        mHistoryDao.setCallback(this);
        listHistory();
    }

    public static HistoryPresenter getsHistoryPresenter() {
        if (sHistoryPresenter == null) {
            synchronized (HistoryPresenter.class) {
                if (sHistoryPresenter == null) {
                    sHistoryPresenter = new HistoryPresenter();
                }
            }
        }
        return sHistoryPresenter;
    }

    @Override
    public void listHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.listHistory();
                }

            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @Override
    public void addHistory(Track track) {
        //需要盘算是否大于100条
        if (mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            isDoDelOutOfSize = true;
            this.mCurrentAddTrack = track;
            //先删除最前的一条记录再添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));

        } else {
            doAddHistory(track);
        }


    }

    private void doAddHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @Override
    public void cleanHistory() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        //ui注册过来的
        if (!mCallbacks.contains(iHistoryCallback)) {
            mCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(IHistoryCallback iHistoryCallback) {
        //删除ui的回调接口
        mCallbacks.remove(iHistoryCallback);
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //nothing to do .
        listHistory();

    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //nothing to do .
        if (isDoDelOutOfSize&&mCurrentAddTrack!=null) {
            isDoDelOutOfSize=false;
            //添加当前的数据
            addHistory(mCurrentAddTrack);

        } else {
            listHistory();
        }
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        this.mCurrentHistories = tracks;
        //通知ui更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (IHistoryCallback callback : mCallbacks) {
                    callback.onHistoryLoaded(tracks);
                }
            }
        });

    }

    @Override
    public void onHistoryClean(boolean isSuccess) {
        //nothing to do .
        listHistory();
    }
}
