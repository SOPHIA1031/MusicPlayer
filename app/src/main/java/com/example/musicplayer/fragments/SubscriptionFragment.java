package com.example.musicplayer.fragments;

import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.R;
import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseFragment;
import com.example.musicplayer.interfaces.ISubscriptionCallback;
import com.example.musicplayer.interfaces.ISubscriptionPresenter;
import com.example.musicplayer.presenters.SubscriptionPresenter;
import com.example.musicplayer.utils.Constants;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.Collections;
import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback {
    private ISubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private RecomendListAdapter mRecomendListAdapter;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView=layoutInflater.inflate(R.layout.fragment_subscription,container,false);
        TwinklingRefreshLayout refreshLayout=rootView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        mSubListView=rootView.findViewById(R.id.sub_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        mSubListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top= UIUtil.dip2px(view.getContext(),5);
                outRect.bottom= UIUtil.dip2px(view.getContext(),5);
                outRect.left= UIUtil.dip2px(view.getContext(),5);
                outRect.right= UIUtil.dip2px(view.getContext(),5);

            }
        });
        //
        mRecomendListAdapter=new RecomendListAdapter();
        mSubListView.setAdapter(mRecomendListAdapter);
        mSubscriptionPresenter= SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();

        return rootView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onSubListLoaded(List<Album> albums) {
        //更新UI
        if (mRecomendListAdapter != null) {
            mRecomendListAdapter.setData(albums);
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getActivity(),"订阅数量不能超过"+ Constants.MAX_SUB_COUNT,Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        //取消接口的注册，避免内存泄漏
        if(mSubscriptionPresenter!=null){
            mSubscriptionPresenter.unRegisterViewCallback(this);
        }
    }
}
