package com.example.cyk.cloudweather.gson;

public class Rain {
    public String status;

    public Grid_minute_forecast grid_minute_forecast;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Grid_minute_forecast getGrid_minute_forecast() {
        return grid_minute_forecast;
    }

    public void setGrid_minute_forecast(Grid_minute_forecast grid_minute_forecast) {
        this.grid_minute_forecast = grid_minute_forecast;
    }
}
