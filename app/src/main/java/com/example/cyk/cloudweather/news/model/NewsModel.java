package com.example.cyk.cloudweather.news.model;

import com.example.cyk.cloudweather.bean.NewsBean;
import com.example.cyk.cloudweather.http.API;
import com.example.cyk.cloudweather.http.RetrofitHelper;

import rx.Subscriber;
import rx.schedulers.Schedulers;

public class NewsModel implements INewsModel {

    @Override
    public void loadNews(final String hostType, final int startPage, final String id,
                         final INewsLoadListener iNewsLoadListener) {
        RetrofitHelper retrofitHelper = new RetrofitHelper(API.NEWS_HOST);
        retrofitHelper.getNews(hostType, id, startPage)
                .observeOn(AndroidScheduler.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<NewsBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        iNewsLoadListener.fail(e);
                    }

                    @Override
                    public void onNext(NewsBean newsBean) {
                        if (startPage != 0) {
                            iNewsLoadListener.loadMoreSuccess(newsBean);
                        } else {
                            iNewsLoadListener.success(newsBean);
                        }

                    }
                });
    }
}
