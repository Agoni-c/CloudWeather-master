package com.example.cyk.cloudweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * 生活指数
 */
public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    public class Comfort{
        @SerializedName("brf")
        public String index;

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("sport")
    public Sport sport;

    public class Sport{
        @SerializedName("brf")
        public String index;

        @SerializedName("txt")
        public String info;
    }

    @SerializedName("cw")
    public CarWash carWash;

    public class CarWash{
        @SerializedName("brf")
        public String index;

        @SerializedName("txt")
        public String info;
    }


}