package com.example.musicplayer.presenters;

import com.example.musicplayer.api.ISubDaoCallback;
import com.example.musicplayer.api.SubscriptionDao;
import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.interfaces.ISubscriptionCallback;
import com.example.musicplayer.interfaces.ISubscriptionPresenter;
import com.example.musicplayer.utils.Constants;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {
    private static SubscriptionPresenter sInstance = null;
    private final SubscriptionDao mSubscriptionDao;
    private Map<Long, Album> mData = new HashMap<>();
    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();

    private SubscriptionPresenter() {
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);

    }

    public static ISubscriptionPresenter getInstance() {
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class) {
                sInstance = new SubscriptionPresenter();
            }
        }

        return sInstance;
    }

    private void listSubscriptions() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                //只调用，不处理结果
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void addSubscription(Album album) {
        //判断当前的订阅数量，不能超过100个
        if (mData.size() >= Constants.MAX_SUB_COUNT) {
            //给出提示
            for (ISubscriptionCallback callback : mCallbacks) {
                callback.onSubFull();
            }
            return;
        }
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @Override
    public void deleteSubscription(Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();

    }

    @Override
    public void getSubscriptionList() {
        listSubscriptions();

    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        //不为空，表示已经订阅
        return result != null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (!mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unRegisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        mCallbacks.remove(iSubscriptionCallback);
    }

    @Override
    public void onAddResult(boolean isSuccess) {
        //添加结果的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(boolean isSuccess) {
        //删除订阅的回调
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });

    }

    @Override
    public void onSubListLoaded(List<Album> result) {
        //加载数据的回调
        mData.clear();
        for (Album album : result) {
            mData.put(album.getId(), album);
        }
        //通知UI更新
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubListLoaded(result);
                }
            }
        });
    }
}
