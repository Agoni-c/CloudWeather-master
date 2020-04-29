package com.example.cyk.cloudweather.news;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.bean.HeadlineNewsBean;

import com.example.cyk.cloudweather.bean.NewsBean;
import com.example.cyk.cloudweather.json.NewsJson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment {
    private static final String TAG = "NewsListFragment";
    private String type;
    private SwipeRefreshLayout srl_news;
    private RecyclerView rv_news;
    private ItemNewsAdapter adapter;
    private HeadlineNewsBean headlineNewsBean;
    private List<NewsBean> newsBeanList;
    private LinearLayoutManager layoutManager;

    public static NewsListFragment newInstance(String type) {
        Bundle args = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        args.putString("type", type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_news_list, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = getArguments().getString("type");
        rv_news = view.findViewById(R.id.rv_news);
        adapter = new ItemNewsAdapter(this.getActivity());
        srl_news = view.findViewById(R.id.srl_news);
        srl_news.setColorSchemeColors(Color.parseColor("#ffce3d3a"));
        srl_news.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNews(type);
                srl_news.setRefreshing(false);
            }
        });
        Log.i(TAG, "onViewCreated: "+type);
        rv_news.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        (layoutManager.findLastVisibleItemPosition() + 1) == layoutManager.getItemCount()) {
                    loadMore();
                }
            }
        });
        initData();
    }

    private void loadNews(String type){
        switch (type) {
            case NewsFragment.NEWS_TYPE_TOP:
                requestNews(NewsFragment.NEWS_TYPE_TOP);
                showNews();
                break;
            case NewsFragment.NEWS_TYPE_YULE:
                requestNews(NewsFragment.NEWS_TYPE_YULE);
                showNews();
                break;
            case NewsFragment.NEWS_TYPE_KEJI:
                requestNews(NewsFragment.NEWS_TYPE_KEJI);
                showNews();
                break;
            case NewsFragment.NEWS_TYPE_TIYU:
                requestNews(NewsFragment.NEWS_TYPE_TIYU);
                showNews();
                break;
        }
    }

    /**
     * 根据新闻类型请求新闻数据
     */
    public void requestNews(final String type){
        String newsUrl = "http://v.juhe.cn/toutiao/index?type="+type+"&key=a7722eda2d2f5d1e84b223ef96197175";

            try {
                URL url = new URL(newsUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                connection.setReadTimeout(5000);
                InputStream in = connection.getInputStream();
                String response = convertStreamToString(in);
                headlineNewsBean = NewsJson.getNewsResponse(response);
                newsBeanList = headlineNewsBean.newsBeanList;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public void showNews() {
        Log.i("list", "showNews: " + newsBeanList.size());
        adapter.setData(newsBeanList);
        layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        rv_news.setLayoutManager(layoutManager);
        rv_news.setAdapter(adapter);
    }

    private void initData() {
        loadNews(type);
    }

    private void loadMore() {
        loadNews(type);
        showMoreNews();
    }


    public void showMoreNews() {
        adapter.addData(newsBeanList);
        adapter.notifyDataSetChanged();
    }

}
