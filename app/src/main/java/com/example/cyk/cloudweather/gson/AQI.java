package com.example.cyk.cloudweather.gson;

/**
 * 空气质量
 */
public class AQI {
    public AQICity city;

    public class AQICity{
        public String aqi;

        public String pm25;

        public String qlty;
    }
}
