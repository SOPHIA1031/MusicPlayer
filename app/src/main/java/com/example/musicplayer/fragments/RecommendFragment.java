package com.example.musicplayer.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseFragment;
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

public class RecommendFragment extends BaseFragment {
    private static final String TAG="RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecomendListAdapter mRecommendListAdapter;
    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        // View加载完成
        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);
        // RecyclerView的使用
        mRecommendRv = mRootView.findViewById(R.id.recommend_list); //找到控件
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext()); //创建布局管理器
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager); //设置布局管理器
        mRecommendListAdapter =new RecomendListAdapter(); //创建适配器
        mRecommendRv.setAdapter(mRecommendListAdapter);

        //获取数据
        getRecommendData();
        // 返回view给界面显示
        return mRootView;
    }

    //获取推荐内容（SDK文档接口3.10.6）
    private void getRecommendData(){
        Map<String, String> map = new HashMap<>();
        // 一页数据的返回数量
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT+"");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>(){
            //回调函数,手机记得联网
            @Override
            public void onSuccess(@Nullable GussLikeAlbumList gussLikeAlbumList){
                if(gussLikeAlbumList!=null){
                    List<Album> albumList=gussLikeAlbumList.getAlbumList();
                    upRecommendUI(albumList); //获取到数据后更新UI
                }
            }
            @Override
            public void onError(int i,String s){
                //报错
                LogUtil.d(TAG,"ERROR"+i+"-->"+s);
            }
        });
    }

    private void upRecommendUI(List<Album> albumList){
        //把数据设置给适配器并且更新UI
        mRecommendListAdapter.setData(albumList);
    }
}
