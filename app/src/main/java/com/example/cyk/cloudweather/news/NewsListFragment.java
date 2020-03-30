package com.example.cyk.cloudweather.news;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.cyk.cloudweather.R;
import com.example.cyk.cloudweather.bean.NewsBean;
import com.example.cyk.cloudweather.news.presenter.NewsPresenter;
import com.example.cyk.cloudweather.news.view.INewsView;

import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * create an instance of this fragment.
 */
public class NewsListFragment extends Fragment implements INewsView {
    private static final String TAG = "NewsListFragment";
    private NewsPresenter presenter;
    private int type;
    private SwipeRefreshLayout srl_news;
    private RecyclerView rv_news;
    private ItemNewsAdapter adapter;
    private List<NewsBean.Bean> newsBeanList;
    private LinearLayoutManager layoutManager;
    private int startPage = 0;

    public static NewsListFragment newInstance(int type) {
        Bundle args = new Bundle();
        NewsListFragment fragment = new NewsListFragment();
        args.putInt("type", type);
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
        type = getArguments().getInt("type");
        rv_news = view.findViewById(R.id.rv_news);
        adapter = new ItemNewsAdapter(this.getActivity());
        srl_news = view.findViewById(R.id.srl_news);
        srl_news.setColorSchemeColors(Color.parseColor("#ffce3d3a"));
        srl_news.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.loadNews(type, 0);
            }
        });
        presenter = new NewsPresenter(this);
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

    private void initData() {
        presenter.loadNews(type, 0);
    }

    private void loadMore() {
        startPage += 20;
        presenter.loadNews(type, startPage);
    }

    @Override
    public void showNews(final NewsBean newsBean) {
        switch (type) {
            case NewsFragment.NEWS_TYPE_TOP:
                newsBeanList = newsBean.getTop();
                break;
            case NewsFragment.NEWS_TYPE_CAR:
                newsBeanList = newsBean.getCar();
                break;
            case NewsFragment.NEWS_TYPE_JOKES:
                newsBeanList = newsBean.getJoke();
                break;
            case NewsFragment.NEWS_TYPE_NBA:
                newsBeanList = newsBean.getNba();
                break;
        }
        Log.i("list", "showNews: " + newsBeanList.size());
        adapter.setData(newsBeanList);
        layoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        rv_news.setLayoutManager(layoutManager);
        rv_news.setAdapter(adapter);


    }

    @Override
    public void showMoreNews(NewsBean newsBean) {
        switch (type) {
            case NewsFragment.NEWS_TYPE_TOP:
                adapter.addData(newsBean.getTop());
                break;
            case NewsFragment.NEWS_TYPE_CAR:
                adapter.addData(newsBean.getCar());
                break;
            case NewsFragment.NEWS_TYPE_JOKES:
                adapter.addData(newsBean.getJoke());
                break;
            case NewsFragment.NEWS_TYPE_NBA:
                adapter.addData(newsBean.getNba());
                break;
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public void hideDialog() {
        srl_news.setRefreshing(false);
    }

    @Override
    public void showDialog() {
        srl_news.setRefreshing(true);
    }

    @Override
    public void showErrorMsg(Throwable throwable) {

        adapter.notifyItemRemoved(adapter.getItemCount());

        Toast.makeText(getContext(), "加载出错:" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
    }
}
