package com.example.cyk.cloudweather.gson;

public class Air {
    public String status;
    public Air_now_city air_now_city;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Air_now_city getAir_now_city() {
        return air_now_city;
    }

    public void setAir_now_city(Air_now_city air_now_city) {
        this.air_now_city = air_now_city;
    }
}
