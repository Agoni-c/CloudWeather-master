package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

public class Alarm_item {
    //	等级
    @SerializedName("level")
    public String level;

    //标题
    @SerializedName("title")
    public String title;

    //内容
    @SerializedName("txt")
    public String txt;

    //类型
    @SerializedName("type")
    public String type;
}
