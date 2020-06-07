package com.example.musicplayer.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.DetailActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.base.BaseFragment;
import com.example.musicplayer.interfaces.ISubscriptionCallback;
import com.example.musicplayer.interfaces.ISubscriptionPresenter;
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.example.musicplayer.presenters.SubscriptionPresenter;
import com.example.musicplayer.utils.Constants;
import com.example.musicplayer.views.ConfirmDialog;
import com.example.musicplayer.views.UIloader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.Collections;
import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, RecomendListAdapter.onRecItemClickListener, RecomendListAdapter.OnAlbumItemLongClickListener, ConfirmDialog.OnDialogActionClickListener {
    private ISubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mSubListView;
    private RecomendListAdapter mRecomendListAdapter;
    private Album mCurrentClickAlbum=null;
    private UIloader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView=(FrameLayout)layoutInflater.inflate(R.layout.fragment_subscription,container,false);
        if (mUiLoader == null) {
            mUiLoader=new UIloader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    //创建一个新的
                    View emptyView=LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view,this,false);
                    TextView tipsView=emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.no_sub_content_tips_text);
                    return emptyView;
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }


        return rootView;
    }

    private View createSuccessView() {
        View itemView=LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_subscription,null);
        TwinklingRefreshLayout refreshLayout=itemView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableRefresh(false);
        mSubListView=itemView.findViewById(R.id.sub_list);
        mSubListView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
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
        mRecomendListAdapter.setOnRecItemClickListener(this);
        mRecomendListAdapter.setOnAlbumItemLongClickListener(this);
        mSubListView.setAdapter(mRecomendListAdapter);
        mSubscriptionPresenter= SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UIloader.UIStatus.LOADING);
        }

        return itemView;
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        //给出取消订阅的提示
        Toast.makeText(BaseApplication.getAppContext(),isSuccess?R.string.cancel_sub_success:R.string.cancel_sub_failed,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubListLoaded(List<Album> albums) {

        if (albums.size()==0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UIloader.UIStatus.EMPTY);
            }

        }else{
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UIloader.UIStatus.SUCCESS);
            }
        }
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
        mRecomendListAdapter.setOnRecItemClickListener(null);
    }

    @Override
    public void onItemClick(int position, Album album) {
        AlbumDetailPresenter.getInstance().setTargetAlbum(album);
        //item被点击了，跳转到详情界面
        Intent intent=new Intent(getContext(), DetailActivity.class);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(Album album) {
        this.mCurrentClickAlbum=album;
        //这是订阅的item被长按了
        //Toast.makeText(BaseApplication.getAppContext(),"订阅被长按.",Toast.LENGTH_SHORT).show();
        ConfirmDialog confirmDialog=new ConfirmDialog(getActivity());
        confirmDialog.setOnDialogActionClickListener(this);
        confirmDialog.show();
    }

    @Override
    public void onCancelSubClick() {
        //取消订阅内容
        if (mCurrentClickAlbum != null&&mSubscriptionPresenter!=null){
            mSubscriptionPresenter.deleteSubscription(mCurrentClickAlbum);
        }


    }

    @Override
    public void onGiveUpClick() {
        //放弃取消订阅

    }
}
