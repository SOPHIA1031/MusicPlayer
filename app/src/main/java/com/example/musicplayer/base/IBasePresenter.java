package com.example.musicplayer.base;

public interface IBasePresenter<T> {

    // 注册UI的回调接口
    void registerViewCallback(T t);
    // 取消注册
    void unRegisterViewCallback(T t);
}
