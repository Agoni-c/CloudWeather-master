package com.example.cyk.cloudweather.news;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.cyk.cloudweather.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment {
    //新闻类型
    public static final String NEWS_TYPE_TOP = "top";
    public static final String NEWS_TYPE_YULE = "yule";
    public static final String NEWS_TYPE_KEJI = "keji";
    public static final String NEWS_TYPE_TIYU= "tiyu";
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentTitles = new ArrayList<>();
    private TabLayout tl_news;
    private ViewPager vp_news;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tl_news = (TabLayout) view.findViewById(R.id.news_tab_layout);
        vp_news = (ViewPager) view.findViewById(R.id.vp_news);
        setViewPager();
        //预加载界面数
        vp_news.setOffscreenPageLimit(3);
        tl_news.setupWithViewPager(vp_news);
    }

    private void setViewPager() {
        fragments.add(NewsListFragment.newInstance(NEWS_TYPE_TOP));
        fragments.add(NewsListFragment.newInstance(NEWS_TYPE_YULE));
        fragments.add(NewsListFragment.newInstance(NEWS_TYPE_KEJI));
        fragments.add(NewsListFragment.newInstance(NEWS_TYPE_TIYU));
        fragmentTitles.add("头条");
        fragmentTitles.add("娱乐");
        fragmentTitles.add("科技");
        fragmentTitles.add("体育");
        MyFragmentAdapter adapter=new MyFragmentAdapter(getChildFragmentManager(),
                fragments,fragmentTitles);
        vp_news.setAdapter(adapter);
    }

}
