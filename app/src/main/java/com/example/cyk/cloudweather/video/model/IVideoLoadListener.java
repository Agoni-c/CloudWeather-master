package com.example.cyk.cloudweather.video.model;

import com.example.cyk.cloudweather.bean.TodayContentBean;
import com.example.cyk.cloudweather.bean.VideoUrlBean;

import java.util.List;

public interface IVideoLoadListener {
    void videoUrlSuccess(List<VideoUrlBean> videoUrlBeans, List<TodayContentBean> contentBeans);
    void fail(Throwable throwable);
}
