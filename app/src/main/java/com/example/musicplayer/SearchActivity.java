package com.example.musicplayer;

import android.content.Context;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseActivity;
import com.example.musicplayer.interfaces.ISearchCallback;
import com.example.musicplayer.presenters.SearchPresenter;
import com.example.musicplayer.utils.LogUtil;
import com.example.musicplayer.views.FlowTextLayout;
import com.example.musicplayer.views.UIloader;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SearchActivity extends BaseActivity implements ISearchCallback {
    private static final String TAG = "SearchActivity";
    private View mBackBtn;
    private EditText mInputBox;
    private View mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private UIloader mContent;
    private RecyclerView mResultListView;
    private RecomendListAdapter mRecomendListAdapter;
    private UIloader mUILoader;

//    private FlowTextLayout mFlowTextLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mSearchPresenter = SearchPresenter.getSearchPresenter();
        //注册UI更新的接口
        mSearchPresenter.registerViewCallback(this);
        //去拿热词
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            //干掉UI更新的接口
            mSearchPresenter.unRegisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initEvent() {
        mUILoader.setOnRetryClickListener(new UIloader.onRetryClickListener() {
            @Override
            public void onRetryClick() {
                if (mSearchPresenter != null) {
                    mSearchPresenter.reSearch();
                    mUILoader.updateStatus(UIloader.UIStatus.LOADING);
                }
            }
        });
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //去调用搜索的逻辑
                String keyword = mInputBox.getText().toString().trim();
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(keyword);
                    mContent.updateStatus(UIloader.UIStatus.LOADING);
                }
            }
        });
        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //  LogUtil.d(TAG, "content -- > " + s);
                //  LogUtil.d(TAG, "start -- > " + start);
                //  LogUtil.d(TAG, "before -- > " + before);
                //  LogUtil.d(TAG, "count -- > " + count);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        /*mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                Toast.makeText(SearchActivity.this, text, Toast.LENGTH_SHORT).show();
            }
        });*/

    }

    private void initView() {
        mBackBtn = this.findViewById(R.id.search_back);
        mInputBox = this.findViewById(R.id.search_input);
        mSearchBtn = this.findViewById(R.id.search_btn);
        mResultContainer = this.findViewById((R.id.search_container));
//        mFlowTextLayout = this.findViewById(R.id.flow_text_layout);
        if (mContent == null) {
            mContent = new UIloader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }
            };
            if (mContent.getParent() instanceof ViewGroup) {
                ((ViewGroup) mContent.getParent()).removeView(mContent);
            }
            mResultContainer.addView(mContent);

        }
    }

    /**
     * 创建数据请求成功的View
     *
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_result_layout, null);
        mResultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(layoutManager);
        //设置适配器
        mRecomendListAdapter = new RecomendListAdapter();
        mResultListView.setAdapter(mRecomendListAdapter);
        return resultView;
    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        //隐藏键盘
        InputMethodManager imm= (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mInputBox.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        if (result != null) {
            if (result.size() == 0) {
                //数据为空
                if (mContent != null) {
                    mContent.updateStatus(UIloader.UIStatus.EMPTY);
                }

            } else {
                //如果数据不为空，那么就设置数据
                mRecomendListAdapter.setData(result);
                mContent.updateStatus(UIloader.UIStatus.SUCCESS);
            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWordList) {
        LogUtil.d(TAG, "hotWordList -- > " + hotWordList.size());
        List<String> hotWords = new ArrayList<>();
        hotWords.clear();
        for (HotWord hotWord : hotWordList) {
            String searchWord = hotWord.getSearchword();
            hotWords.add(searchWord);
        }
        Collections.sort(hotWords);
        //更新UI
//        mFlowTextLayout.setTextContents(hotWords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {

    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUILoader != null) {
            mUILoader.updateStatus(UIloader.UIStatus.NETWORK_ERROR);
        }

    }
}
