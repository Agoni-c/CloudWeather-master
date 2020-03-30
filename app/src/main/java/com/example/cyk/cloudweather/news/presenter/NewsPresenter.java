package com.example.cyk.cloudweather.news.presenter;

import com.example.cyk.cloudweather.bean.NewsBean;
import com.example.cyk.cloudweather.http.API;
import com.example.cyk.cloudweather.news.NewsFragment;
import com.example.cyk.cloudweather.news.model.INewsLoadListener;
import com.example.cyk.cloudweather.news.model.INewsModel;
import com.example.cyk.cloudweather.news.model.NewsModel;
import com.example.cyk.cloudweather.news.view.INewsView;

public class NewsPresenter implements INewsPresenter, INewsLoadListener {

    private INewsModel iNewsModel;
    private INewsView iNewsView;

    public NewsPresenter(INewsView iNewsView) {
        this.iNewsView = iNewsView;
        this.iNewsModel = new NewsModel();
    }

    @Override
    public void loadNews(int type, final int startPage) {
        if (startPage == 0) {
            iNewsView.showDialog();
        }

//        switch (type) {
//            case NewsFragment.NEWS_TYPE_TOP:
//                iNewsModel.loadNews("headline", startPage, API.HEADLINE_ID,
//                        this);
//                break;
//            case NewsFragment.NEWS_TYPE_CAR:
//                iNewsModel.loadNews("headline", startPage, API.CAR_ID,
//                        this);
//                break;
//            case NewsFragment.NEWS_TYPE_JOKES:
//                iNewsModel.loadNews("list", startPage, API.JOKE_ID,
//                        this);
//                break;
//            case NewsFragment.NEWS_TYPE_NBA:
//                iNewsModel.loadNews("list", startPage, API.NBA_ID,
//                        this);
//                break;
//        }
        switch (type) {
            case NewsFragment.NEWS_TYPE_TOP:
                iNewsModel.loadNews("headline", startPage, API.HEADLINE_ID,
                        this);
                break;
            case NewsFragment.NEWS_TYPE_CAR:
                iNewsModel.loadNews("headline", startPage, API.CAR_ID,
                        this);
                break;
            case NewsFragment.NEWS_TYPE_JOKES:
                iNewsModel.loadNews("list", startPage, API.JOKE_ID,
                        this);
                break;
            case NewsFragment.NEWS_TYPE_NBA:
                iNewsModel.loadNews("list", startPage, API.NBA_ID,
                        this);
                break;
        }

    }

    @Override
    public void success(NewsBean newsBean) {
        iNewsView.hideDialog();
        if (newsBean != null) {
            iNewsView.showNews(newsBean);
        }

    }

    @Override
    public void fail(Throwable throwable) {
        iNewsView.hideDialog();
        iNewsView.showErrorMsg(throwable);
    }

    @Override
    public void loadMoreSuccess(NewsBean newsBean) {
        iNewsView.hideDialog();
        iNewsView.showMoreNews(newsBean);
    }
}
