package com.example.cyk.cloudweather.news.view;

import com.example.cyk.cloudweather.bean.NewsBean;

public interface  INewsView {
    void showNews(NewsBean newsBean);

    void showMoreNews(NewsBean newsBean);

    void hideDialog();
    void showDialog();
    void showErrorMsg(Throwable throwable);
}
