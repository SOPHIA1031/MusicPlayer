package com.example.musicplayer.fragments;

import android.content.Intent;
import android.graphics.Canvas;
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

import com.example.musicplayer.PlayerActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapters.DetailListAdapter;
import com.example.musicplayer.base.BaseApplication;
import com.example.musicplayer.base.BaseFragment;
import com.example.musicplayer.interfaces.IHistoryCallback;
import com.example.musicplayer.presenters.HistoryPresenter;
import com.example.musicplayer.presenters.PlayerPresenter;
import com.example.musicplayer.views.ConfirmCheckBoxDialog;
import com.example.musicplayer.views.ConfirmDialog;
import com.example.musicplayer.views.UIloader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class HistoryFragment extends BaseFragment implements IHistoryCallback, DetailListAdapter.ItemClickListener, DetailListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.OnDialogActionClickListener {
    private UIloader mUILoader;
    private DetailListAdapter mDetailListAdapter;
    private HistoryPresenter mHistoryPresenter;
    private Track mCurrentClickHistoryItem = null;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_history, container, false);
        if (mUILoader == null) {
            mUILoader = new UIloader(BaseApplication.getAppContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }

                @Override
                protected View getEmptyView() {
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tips = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tips.setText("历史记录为空哦");
                    return emptyView;
                }
            };
        } else {
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }
        }
        mHistoryPresenter = HistoryPresenter.getsHistoryPresenter();
        mHistoryPresenter.registerViewCallback(this);
        mUILoader.updateStatus(UIloader.UIStatus.LOADING);
        mHistoryPresenter.listHistory();
        rootView.addView(mUILoader);

        return rootView;
    }

    private View createSuccessView(ViewGroup container) {
        View successView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_history, container, false);
        TwinklingRefreshLayout refreshLayout = successView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableRefresh(false);
        refreshLayout.setEnableLoadmore(false);
        refreshLayout.setEnableOverScroll(true);
        RecyclerView historyList = successView.findViewById(R.id.history_list);
        historyList.setLayoutManager(new LinearLayoutManager(container.getContext()));
        //设置item的上下间距
        historyList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 2);
                outRect.right = UIUtil.dip2px(view.getContext(), 2);
            }
        });
        //设置适配器
        mDetailListAdapter = new DetailListAdapter();
        mDetailListAdapter.setItemClickListener(this);
        mDetailListAdapter.setItemLongClickListener(this);
        historyList.setAdapter(mDetailListAdapter);
        return successView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mHistoryPresenter != null) {
            mHistoryPresenter.unRegisterViewCallback(this);
        }
    }

    @Override
    public void onHistoryLoaded(List<Track> tracks) {
        if (tracks == null || tracks.size() == 0) {
            mUILoader.updateStatus(UIloader.UIStatus.EMPTY);
        } else {
            //更新数据
            mDetailListAdapter.setData(tracks);
            mUILoader.updateStatus(UIloader.UIStatus.SUCCESS);
        }


    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setsPlayList(detailData, position);
        //跳转到播放页
        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Track track) {
        this.mCurrentClickHistoryItem = track;
        //去删除历史
        //Toast.makeText(getActivity(), "历史记录长按..."+track.getTrackTitle(), Toast.LENGTH_SHORT).show();
        ConfirmCheckBoxDialog dialog = new ConfirmCheckBoxDialog(getActivity());
        dialog.setOnDialogActionClickListener(this);
        dialog.show();
    }

    @Override
    public void onCancelClick() {
        //nothing to do.
    }

    @Override
    public void onConfirmClick(boolean isCheck) {
        //删除历史
        if (mHistoryPresenter != null && mCurrentClickHistoryItem != null) {
            if (!isCheck) {
                mHistoryPresenter.delHistory(mCurrentClickHistoryItem);
            } else {
                mHistoryPresenter.cleanHistory();
            }

        }

    }
}
