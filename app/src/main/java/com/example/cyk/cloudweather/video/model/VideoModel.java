package com.example.cyk.cloudweather.video.model;

import com.example.cyk.cloudweather.bean.TodayBean;
import com.example.cyk.cloudweather.bean.TodayContentBean;
import com.example.cyk.cloudweather.bean.VideoUrlBean;
import com.example.cyk.cloudweather.http.API;
import com.example.cyk.cloudweather.http.RetrofitHelper;
import com.example.cyk.cloudweather.news.model.AndroidScheduler;
import com.example.cyk.cloudweather.video.presenter.VideoPresenter;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class VideoModel implements IVideoModel {

    @Override
    public void loadVideo(String category, final IVideoLoadListener iVideoLoadListener) {
        final List<VideoUrlBean> videoList = new ArrayList<>();
        final List<TodayContentBean> contentBeans = new ArrayList<>();
        final RetrofitHelper retrofitHelper = new RetrofitHelper(API.TODAY_HOST);

        retrofitHelper.getToday(category)
                .flatMap(new Func1<TodayBean, Observable<VideoUrlBean>>() {
                    @Override
                    public Observable<VideoUrlBean> call(TodayBean todayBean) {
                        return Observable.from(todayBean.getData())
                                .flatMap(new Func1<TodayBean.DataBean, Observable<VideoUrlBean>>() {
                                    @Override
                                    public Observable<VideoUrlBean> call(TodayBean.DataBean dataBean) {
                                        String content = dataBean.getContent();
                                        TodayContentBean contentBean = VideoPresenter.getTodayContentBean(content);
                                        contentBeans.add(contentBean);
                                        String api = VideoPresenter.getVideoContentApi(contentBean.getVideo_id());
                                        return retrofitHelper.getVideoUrl(api);
                                    }
                                });
                    }
                })
                .observeOn(AndroidScheduler.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<VideoUrlBean>() {
                    @Override
                    public void onCompleted() {
                        iVideoLoadListener.videoUrlSuccess(videoList, contentBeans);
                    }

                    @Override
                    public void onError(Throwable e) {
                        iVideoLoadListener.fail(e);
                    }

                    @Override
                    public void onNext(VideoUrlBean videoUrlBean) {
                        videoList.add(videoUrlBean);
                    }
                });
    }
}