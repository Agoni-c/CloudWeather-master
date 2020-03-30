package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 24小时逐小时天气预报
 */
public class Forecast_hourly {
    @SerializedName("cond_code")
    public String code;

    @SerializedName("cond_txt")
    public String hourlyInfo;

    @SerializedName("tmp")
    public String tmp;

    @SerializedName("time")
    public String time;

    @SerializedName("wind_dir")
    public String wind_dir;

    @SerializedName("wind_sc")
    public String wind_sc;
}
