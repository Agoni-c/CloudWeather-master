package com.example.cyk.cloudweather.news.model;

import com.example.cyk.cloudweather.bean.NewsBean;

public interface INewsLoadListener {
    void success(NewsBean newsBean);
    void fail(Throwable throwable);

    void loadMoreSuccess(NewsBean newsBean);
}
