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
import com.example.musicplayer.adapters.ImageAdapter;
import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseFragment;
import com.example.musicplayer.interfaces.IRecommendViewCallback;
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.example.musicplayer.presenters.RecommendPresenter;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.utils.DataBean;
import com.example.musicplayer.utils.LogUtil;
import com.example.musicplayer.views.UIloader;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.youth.banner.Banner;

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
//    private Banner mBanner;
    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        mUIloader=new UIloader(getContext()) {
            @Override
            protected View getSuccessView(ViewGroup container) {
                return createSuccessView(layoutInflater,container);  //显示成功显示的页面
            }
        };

        mRecommendPresenter=RecommendPresenter.getInstance();//获取逻辑层对象
        mRecommendPresenter.registerViewCallback(this);// 设置通知接口的注册
        mRecommendPresenter.getRecommendList(); //获取推荐的数据
        // 返回view给界面显示
        if (mUIloader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUIloader.getParent()).removeView(mUIloader);
        }
        mUIloader.setOnRetryClickListener(this);   //设置重试点击的接口
        return mUIloader;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container){

//        inflate(int resource, ViewGroup root, boolean attachToRoot)
//
//        resource：需要加载布局资源文件的id，意思是需要将这个布局文件中加载到Activity中来操作。
//
//        root：需要附加到resource资源文件的根控件，就是inflate()会返回一个View对象，
//        如果第三个参数attachToRoot为true，就将这个root作为根对象返回，
//        否则仅仅将这个root对象的LayoutParams宽高参数属性附加到resource对象的根布局对象上，
//        也就是布局文件resource的最外层的View上，比如是一个LinearLayout或者其它的Layout对象。
//        如果没有根控件，就写null
//
//        attachToRoot：是否将root附加到布局文件的根视图上（可以写true或false）

        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false); // View加载完成
        mRootView=layoutInflater.inflate(R.layout.fragment_recommend,container,false);// View加载完成
        // RecyclerView的使用
        mRecommendRv = mRootView.findViewById(R.id.recommend_list); //找到控件
        LinearLayoutManager linearLayoutManager =new LinearLayoutManager(getContext()); //创建布局管理器
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager); //设置布局管理器
        // item之间设置间距
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

        mRecommendListAdapter.setOnRecItemClickListener(this);  //设置item点击的监听

        //-----------------轮播图新添加
        //创建（new banner()）或者布局文件中获取banner
//        mBanner =mRootView.findViewById(R.id.banner);
//        //默认直接设置adapter就行了
//        mBanner.setAdapter(new ImageAdapter(DataBean.getTestData3()));
        return mRootView;
    }
    @Override
    public void onRecommendListLoaded(List<Album> result){
        //成功获取到推荐内容，这个方法就会被调用
        //然后更新ui
        mRecommendListAdapter.setData(result);
        mUIloader.updateStatus(UIloader.UIStatus.SUCCESS); //切换到成功界面
    }

    //加载网络错误UI
    @Override
    public void onNetWorkError() {
        mUIloader.updateStatus(UIloader.UIStatus.NETWORK_ERROR);
    }

    //加载数据为空UI
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
