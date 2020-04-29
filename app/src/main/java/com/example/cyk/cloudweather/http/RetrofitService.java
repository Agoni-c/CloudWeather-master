package com.example.cyk.cloudweather.http;

import retrofit2.http.Url;
import rx.Observable;

import com.example.cyk.cloudweather.bean.TodayBean;
import com.example.cyk.cloudweather.bean.VideoUrlBean;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface  RetrofitService {

    /*
     * http://c.m.163.com/nc/article/headline/T1348647909107/0-20.html
     * */
//    @GET("nc/article/{type}/{id}/{startPage}-20.html")
//    Observable<NewsBean> getNews(@Path("type") String type,
//                                 @Path("id") String id,
//                                 @Path("startPage") int startPage);

    /*
     * http://3g.163.com/touch/reconstruct/article/list/BA8E6OEOwangning/0-20.html
     * */
//    @GET("touch/reconstruct/article/{type}/{id}/{startPage}-20.html")
//    Observable<NewsBean> getNews(@Path("type") String type,
//                                 @Path("id") String id,
//                                 @Path("startPage") int startPage);


    /*
     * http://is.snssdk.com/api/news/feed/v51/?category=video
     * */
    @GET("news/feed/v51/")
    Observable<TodayBean> getToday(@Query("category") String category);

    /*
     * http://ib.365yg.com/video/urls/v/1/toutiao/mp4/v02004f00000bbpbk3l2v325q7lmkds0?r=6781281688452415&s=2734808831
     * */
    @GET
    Observable<VideoUrlBean> getVideoUrl(@Url String url);
}
