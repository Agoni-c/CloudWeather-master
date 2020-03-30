package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 生活指数
 */
public class Lifestyle {

    @SerializedName("type")
    public String type;

    @SerializedName("brf")
    public String brf;

    @SerializedName("txt")
    public String text;


}
