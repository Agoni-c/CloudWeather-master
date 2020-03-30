package com.example.cyk.cloudweather.db;

import org.litepal.crud.LitePalSupport;

public class City extends LitePalSupport{
    private int id;
    private String cityName;//市
    private int cityCode;//城市代号
    private int provinceId;//省id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }

    public int getCityCode() {
        return cityCode;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
