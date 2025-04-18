package com.radioactives.krushimitra.modal;

public class ForecastItem {
    private String time;
    private String temperature;
    private String icon;

    public ForecastItem(String time, String temperature, String icon) {
        this.time = time;
        this.temperature = temperature;
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getIcon() {
        return icon;
    }
}
