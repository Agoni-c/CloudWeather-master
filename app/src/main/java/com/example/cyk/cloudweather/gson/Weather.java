package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 天气数据
 */
public class Weather {

    public String status;

    public Basic basic;

    public Update update;


    public Now now;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Basic getBasic() {
        return basic;
    }

    public void setBasic(Basic basic) {
        this.basic = basic;
    }


    public Now getNow() {
        return now;
    }

    public void setNow(Now now) {
        this.now = now;
    }


    public List<Forecast> getForecastList() {
        return forecastList;
    }

    public void setForecastList(List<Forecast> forecastList) {
        this.forecastList = forecastList;
    }

    public List<Lifestyle> getLifestyleList() {
        return lifestyleList;
    }

    public void setLifestyleList(List<Lifestyle> lifestyleList) {
        this.lifestyleList = lifestyleList;
    }

    public List<Forecast_hourly> getForecast_hourlyList() {
        return forecast_hourlyList;
    }

    public void setForecast_hourlyList(List<Forecast_hourly> forecast_hourlyList) {
        this.forecast_hourlyList = forecast_hourlyList;
    }

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    @SerializedName("lifestyle")
    public List<Lifestyle> lifestyleList;

    @SerializedName("hourly")
    public List<Forecast_hourly> forecast_hourlyList;

}
