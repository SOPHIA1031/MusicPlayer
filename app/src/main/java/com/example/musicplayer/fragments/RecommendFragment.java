package com.example.musicplayer.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicplayer.DetailActivity;
import com.example.musicplayer.MainActivity;
import com.example.musicplayer.R;
import com.example.musicplayer.adapters.RecomendListAdapter;
import com.example.musicplayer.base.BaseFragment;
import com.example.musicplayer.interfaces.IRecommendViewCallback;
import com.example.musicplayer.presenters.AlbumDetailPresenter;
import com.example.musicplayer.presenters.RecommendPresenter;
import com.example.musicplayer.utils.ImageFactory;
import com.example.musicplayer.views.UIloader;
import com.hacknife.carouselbanner.Banner;
import com.hacknife.carouselbanner.CoolCarouselBanner;
import com.hacknife.carouselbanner.interfaces.OnCarouselItemChangeListener;
import com.hacknife.carouselbanner.interfaces.OnCarouselItemClickListener;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

// View层
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UIloader.onRetryClickListener, RecomendListAdapter.onRecItemClickListener {
    private static final String TAG="RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecomendListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UIloader mUIloader;
    CoolCarouselBanner banner;

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

        //-----------------轮播图添加
        banner = mRootView.findViewById(R.id.banner);
        List<String> list = new ArrayList<>();
        Banner.init(new ImageFactory());
        list.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3893798113,2002765214&fm=26&gp=0.jpg");
        list.add("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=3568579721,2397111734&fm=26&gp=0.jpg");
        list.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3960632197,3985528648&fm=15&gp=0.jpg");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1591247441041&di=6e97172be435f4de037400cd18cd3542&imgtype=0&src=http%3A%2F%2Fhbimg.huabanimg.com%2F59c74fe83f5a77028148c4846c82679b093a5dc21ec2f-LrmZUD_fw658");
        list.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589442267333&di=e58748229ef60811e4c92450ce453899&imgtype=0&src=http%3A%2F%2Fhbimg.huabanimg.com%2F1d28c650199db55aa9602a511803e7377388b956eaf2-LLKbqs_fw658");
        list.add("https://ss1.bdstatic.com/70cFuXSh_Q1YnxGkpoWK1HF6hhy/it/u=3012112261,1934803070&fm=26&gp=0.jpg");

        banner.setOnCarouselItemChangeListener(new OnCarouselItemChangeListener() {
            @Override
            public void onItemChange(int position) {
//                Toast.makeText(getContext(), String.valueOf(position), Toast.LENGTH_LONG).show();
            }
        });
        banner.setOnCarouselItemClickListener(new OnCarouselItemClickListener() {
            @Override
            public void onItemClick(int position, String url) {
                Toast.makeText(getContext(), url, Toast.LENGTH_LONG).show();
            }
        });
        banner.initBanner(list);

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
