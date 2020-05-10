package com.example.musicplayer.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.DetailActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseFragment;
import com.example.musicplayer.interfaces.IRecommendViewCallback;
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.example.musicplayer.presenters.RecommendPresenter;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.LogUtil;
import com.example.musicplayer.views.UIloader;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UIloader.onRetryClickListener, RecomendListAdapter.onRecItemClickListener {
    private static final String TAG="RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecomendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UIloader mUIloader;
    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        mUIloader=new UIloader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);
            }
        };

        //获取逻辑层对象
        mRecommendPresenter=RecommendPresenter.getInstance();
        // 设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();
        // 返回view给界面显示


        if (mUIloader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUIloader.getParent()).removeView(mUIloader);
        }
        mUIloader.setOnRetryClickListener(this);
        return mUIloader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container){
        // View加载完成
        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        // View加载完成
        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        // RecyclerView的使用
        mRecommendRv = mRootView.findViewById(R.id.recommend_list); //找到控件
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext()); //创建布局管理器
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager); //设置布局管理器
        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                outRect.top=8; //单位px
                outRect.bottom=8;
            }
        });

        //创建适配器
        mRecommendListAdapter =new RecomendListAdapter();
        mRecommendRv.setAdapter(mRecommendListAdapter);

        mRecommendListAdapter.setOnRecItemClickListener(this);
        return mRootView;
    }
    @Override
    public void onRecommendListLoaded(List<Album> result){
        //成功获取到推荐内容，这个方法就会被调用
        //然后更新ui
        mRecommendListAdapter.setData(result);
        mUIloader.updateStatus(UIloader.UIStatus.SUCCESS); //切换到成功界面
    }

    @Override
    public void onNetWorkError() {
        mUIloader.updateStatus(UIloader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onDataEmpty() {
        mUIloader.updateStatus(UIloader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUIloader.updateStatus(UIloader.UIStatus.LOADING);
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏
        if(mRecommendPresenter!=null){
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
       // 表示网络不佳用户点了重试
        //重新获取数据
        if(mRecommendPresenter!=null){
            mRecommendPresenter.getRecommendList();
        }
    }

    //专辑点击事件
    @Override
    public void onItemClick(int index ,Album album) {
        //把数据给Presenter
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //跳转到详情界面
        Intent intent =new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }
}
