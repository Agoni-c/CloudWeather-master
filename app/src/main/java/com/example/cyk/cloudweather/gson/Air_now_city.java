package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

public class Air_now_city {
    //	空气质量指数
    @SerializedName("aqi")
    public String aqi;

    //空气质量
    @SerializedName("qlty")
    public String qlty;

    //	主要污染物
    @SerializedName("main")
    public String main;

    //pm25
    @SerializedName("pm25")
    public String pm25;
}
