package com.radioactives.krushimitra.modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {
    @SerializedName("list")
    public List<WeatherItem> list;

    public static class WeatherItem {
        @SerializedName("dt_txt")
        public String dtTxt;

        @SerializedName("main")
        public Main main;

        @SerializedName("weather")
        public List<Weather> weather;
    }

    public static class Main {
        @SerializedName("temp")
        public double temp;
    }

    public static class Weather {
        @SerializedName("icon")
        public String icon;
    }
}
