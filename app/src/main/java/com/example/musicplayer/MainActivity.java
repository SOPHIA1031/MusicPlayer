package com.example.musicplayer;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.example.musicplayer.adapters.IndicatorAdapter;
import com.example.musicplayer.adapters.MainContentAdapter;
import com.example.musicplayer.utils.ImageFactory;
import com.example.musicplayer.utils.LogUtil;
import com.hacknife.carouselbanner.Banner;
import com.hacknife.carouselbanner.CoolCarouselBanner;
import com.hacknife.carouselbanner.interfaces.OnCarouselItemChangeListener;
import com.hacknife.carouselbanner.interfaces.OnCarouselItemClickListener;
//import com.youth.banner.Banner;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

public class MainActivity extends FragmentActivity {

    private static final String TAG="MainActivity";
    private IndicatorAdapter mIndicatorAdapter;
    private ViewPager mContentPager;
    private MagicIndicator mMagicIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);   //给Activity设置一个layout布局
        initView();
        initEvent();
//        useBanner();
    }
    private  void initEvent(){
        mIndicatorAdapter.setOnIndicatorTapClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG,"click index is"+index);
                if (mContentPager!=null){
                    mContentPager.setCurrentItem(index);   //点击导航条时，跳转到对应的页面（封装的方法）
                }
            }
        });
    }
    //最上面的导航条
    private void initView(){
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));

        //创建indicator的适配器
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdjustMode(true); //自适应宽度，三栏平分
        commonNavigator.setAdapter(mIndicatorAdapter);
        // 设置要显示的内容
        mContentPager =this.findViewById(R.id.content_pager);
        //创建内容适配器
        FragmentManager supportFragmentManager=getSupportFragmentManager();
        MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
        mContentPager.setAdapter(mainContentAdapter);
        // 绑定
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);// viewpager有滑动监听，下面的页面滑动上面的也跟着滑动
    }

}
