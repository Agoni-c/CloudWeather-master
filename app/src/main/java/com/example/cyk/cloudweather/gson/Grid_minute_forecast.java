package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

public class Grid_minute_forecast {

    //	预报日期
    @SerializedName("date")
    public String date;

    //降雨邻近预报
    @SerializedName("txt")
    public String txt;

}
