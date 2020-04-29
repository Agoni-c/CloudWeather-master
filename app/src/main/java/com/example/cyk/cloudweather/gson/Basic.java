package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 基础信息
 */
public class Basic {
    @SerializedName("location")
    public String countyName;

    @SerializedName("parent_city")
    public String cityName;

    @SerializedName("admin_area")
    public String provinceName;

    @SerializedName("lon")//经度
    public String lon;

    @SerializedName("lat")//纬度
    public String lat;

    @SerializedName("cid")
    public String weatherId;

}