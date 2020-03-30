package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 实时天气情况
 */
public class Now {
    @SerializedName("tmp")
    public String temperature;//温度

    @SerializedName("cond_code")
    public String code;

    @SerializedName("cond_txt")//天气情况
    public String txt;

    @SerializedName("pres")
    public String pres;

    @SerializedName("vis")
    public String vis;

    @SerializedName("hum")
    public String humidity;//湿度

    @SerializedName("wind_dir")
    public String wind_direction;//风向

    @SerializedName("wind_sc")
    public String wind_strength;//风力

 }
