package com.example.musicplayer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.musicplayer.R;
import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.utils.LogUtil;

public abstract class UIloader extends FrameLayout {
    private View mLoadView;
    private View mSuccessView;
    private View mNetErrView;
    private View mEmptyView;
    private onRetryClickListener mOnRetryClickListener=null;
    //枚举类
    public enum  UIStatus {
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }
    public UIStatus mCurrentStatus =UIStatus.NONE; //默认状态

    public UIloader(@NonNull Context context) {
        this(context,null);
    }

    public UIloader(@NonNull Context context, @Nullable AttributeSet attrs) {
        //保证唯一的入口
        this(context, attrs,0);
    }

    public UIloader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    //初始化UI，把view加载进来
    private void init() {
        switchUIByCurrent();
    }
    public void updateStatus(UIStatus status){
        mCurrentStatus =status;
        //更新UI ,一定要在主线程上，用handler
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrent();
            }
        });
    }
    private void switchUIByCurrent() {
        //加载中
        if(mLoadView==null){
            mLoadView=getLoadingView();
            addView(mLoadView);
        }
        //是否可见
        mLoadView.setVisibility(mCurrentStatus==UIStatus.LOADING?VISIBLE:GONE);

        //成功
        if(mSuccessView==null){
            mSuccessView=getSuccessView(this);
            addView(mSuccessView);
        }
        //是否可见
        mSuccessView.setVisibility(mCurrentStatus==UIStatus.SUCCESS?VISIBLE:GONE);

        //网络错误
        if(mNetErrView==null){
            mNetErrView=getNetErrView();
            addView(mNetErrView);
        }
        //是否可见
        mNetErrView.setVisibility(mCurrentStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);

        //数据为空
        if(mEmptyView==null){
            mEmptyView=getEmptyView();
            addView(mEmptyView);
        }
        //是否可见
        mEmptyView.setVisibility(mCurrentStatus==UIStatus.EMPTY?VISIBLE:GONE);
    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
    }

    private View getNetErrView() {
        View netErrView=LayoutInflater.from(getContext()).inflate(R.layout.fragment_net_err_view,this,false);
        netErrView.findViewById(R.id.net_err_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新获取数据
                if(mOnRetryClickListener!=null){
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });//找到控件
        return netErrView;
    }

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view,this,false);
    }
    protected  abstract View getSuccessView(ViewGroup container);

    public void setOnRetryClickListener(onRetryClickListener listener) {
        this.mOnRetryClickListener=listener;
    }
    public interface onRetryClickListener{
        void onRetryClick();
    }
}
