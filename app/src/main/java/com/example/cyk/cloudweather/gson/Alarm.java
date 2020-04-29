package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Alarm {
    public String status;

    @SerializedName("alarm")
    public List<Alarm_item> Alarm_item;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<com.example.cyk.cloudweather.gson.Alarm_item> getAlarm_item() {
        return Alarm_item;
    }

    public void setAlarm_item(List<com.example.cyk.cloudweather.gson.Alarm_item> alarm_item) {
        Alarm_item = alarm_item;
    }
}
