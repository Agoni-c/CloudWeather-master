package com.example.cyk.cloudweather;

import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.cyk.cloudweather.news.NewsFragment;
import com.example.cyk.cloudweather.video.VideoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {


    private BottomNavigationView navigationView;

    //创建三个Fragment和存放他们的数组
    private Fragment newsFragment;
    private Fragment videoFragment;
    private Fragment weatherFragment;
    public Fragment[] fragmentlist;

    //用于标识上一个fragment
    private int lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //fragment初始化
        initFragment();
        setHalfTransparent();
    }

    /**
     * 初始化fragment，并将headFragment显示出来
     */
    private void initFragment() {
        navigationView = (BottomNavigationView) findViewById(R.id.navigation);
        //配置菜单按钮显示图标
        navigationView.setItemIconTintList(null);
        //将三个fragment先放在数组里
        newsFragment = new NewsFragment();
        weatherFragment = new WeatherFragment();
        videoFragment = new VideoFragment();
        fragmentlist = new Fragment[]{weatherFragment, newsFragment, videoFragment};
        //此时标识标识首页
        //0表示首页，1表示orderFragment，2表示userFragment
        lastFragment = 0;
        //为navigationView设置点击事件
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //设置默认页面为weatherFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout_main, weatherFragment)
                .show(weatherFragment).commit();
        navigationView.setSelectedItemId(R.id.navigation_weather);
    }

    /**
     * 给BottomNavigationView添加按钮的点击事件
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            //每次点击后都将所有图标重置到默认不选中图片
            resetToDefaultIcon();

            switch (item.getItemId()) {
                case R.id.navigation_weather:
                    //判断要跳转的页面是否是当前页面，若是则不做动作
                    if (lastFragment != 0) {
                        switchFragment(lastFragment, 0);
                        lastFragment = 0;
                    }
                    //设置按钮的
                    item.setIcon(R.mipmap.weatherselected);
                    return true;
                case R.id.navigation_news:
                    if (lastFragment != 1) {
                        switchFragment(lastFragment, 1);
                        lastFragment = 1;
                    }
                    item.setIcon(R.mipmap.newsselected);
                    return true;
                case R.id.navigation_video:
                    if (lastFragment != 2) {
                        switchFragment(lastFragment, 2);
                        lastFragment = 2;
                    }
                    item.setIcon(R.mipmap.videoselected);
                    return true;
            }
            return false;
        }
    };

    /**
     *
     * @param lastFragment 表示点击按钮前的页面
     * @param index 表示点击按钮对应的页面
     */
    private void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //隐藏上个Fragment
        transaction.hide(fragmentlist[lastFragment]);

        //判断transaction中是否加载过index对应的页面，若没加载过则加载
        if (fragmentlist[index].isAdded() == false) {
            transaction.add(R.id.frame_layout_main, fragmentlist[index]);
        }
        //根据角标将fragment显示出来
        transaction.show(fragmentlist[index]).commitAllowingStateLoss();
    }


    /**
     * 重新配置每个按钮的图标
     */
    private void resetToDefaultIcon() {
        navigationView.getMenu().findItem(R.id.navigation_weather).setIcon(R.mipmap.weather);
        navigationView.getMenu().findItem(R.id.navigation_news).setIcon(R.mipmap.news);
        navigationView.getMenu().findItem(R.id.navigation_video).setIcon(R.mipmap.video);
    }
    /**
     * 半透明状态栏
     */
    protected void setHalfTransparent() {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

}
