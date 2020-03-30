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

    public static final int NEWS_TYPE_TOP=0;
    public static final int NEWS_TYPE_CAR = 1;
    public static final int NEWS_TYPE_NBA = 2;
    public static final int NEWS_TYPE_JOKES = 3;
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
        fragments.add(NewsListFragment.newInstance(NEWS_TYPE_CAR));
        fragments.add(NewsListFragment.newInstance(NEWS_TYPE_NBA));
        fragments.add(NewsListFragment.newInstance(NEWS_TYPE_JOKES));
        fragmentTitles.add("头条");
        fragmentTitles.add("汽车");
        fragmentTitles.add("体育");
        fragmentTitles.add("笑话");
        MyFragmentAdapter adapter=new MyFragmentAdapter(getChildFragmentManager(),
                fragments,fragmentTitles);
        vp_news.setAdapter(adapter);
    }

}
