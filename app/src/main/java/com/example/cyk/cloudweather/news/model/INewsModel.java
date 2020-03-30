package com.example.cyk.cloudweather.news.model;

public interface INewsModel {
    void loadNews(String hostType,
                  int startPage,
                  String id,
                  INewsLoadListener iNewsLoadListener);
}
