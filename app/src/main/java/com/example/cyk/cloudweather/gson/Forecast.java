package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 未来7日天气预测
 */
public class Forecast {

    //	预报日期
    @SerializedName("date")
    public String date;

    //白天天气状况代码
    @SerializedName("cond_code_d")
    public String cond_code_d;

    //夜间天气状况代码
    @SerializedName("cond_code_n")
    public String cond_code_n;

    //白天天气状况描述
    @SerializedName("cond_txt_d")
    public String cond_txt_d;

    //夜间天气状况描述
    @SerializedName("cond_txt_n")
    public String cond_txt_n;

    //最高温度
    @SerializedName("tmp_max")
    public String max;

    //最低温度
    @SerializedName("tmp_min")
    public String min;

    //风向
    @SerializedName("wind_dir")
    public String wind_dir;

    //风力
    @SerializedName("wind_sc")
    public String wind_sc;
}
