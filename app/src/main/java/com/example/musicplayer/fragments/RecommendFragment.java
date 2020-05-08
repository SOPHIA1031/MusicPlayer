package com.example.musicplayer.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseFragment;
import com.example.musicplayer.interfaces.IRecommendViewCallback;
import com.example.musicplayer.presenters.RecommendPresenter;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {
    private static final String TAG="RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecomendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
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
        //获取逻辑层对象
        mRecommendPresenter=RecommendPresenter.getInstance();
        // 设置通知接口的注册
        mRecommendPresenter.registerViewCallback(this);
        mRecommendPresenter.getRecommendList();
        // 返回view给界面显示
        return mRootView;
    }


    @Override
    public void onRecommendListLoaded(List<Album> result){
        //成功获取到推荐内容，这个方法就会被调用
        //然后更新ui
        mRecommendListAdapter.setData(result);
    }

    @Override
    public void onLoadMore(List<Album> result){

    }

    @Override
    public void onRefreshMore(List<Album> result){}

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏
        if(mRecommendPresenter!=null){
            mRecommendPresenter.unRegisterViewCallback(this);
        }
    }
}
