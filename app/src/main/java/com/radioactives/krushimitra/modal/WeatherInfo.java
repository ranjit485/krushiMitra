package com.radioactives.krushimitra.modal;

public class WeatherInfo {
    public String cityName;
    public double temperature;
    public int humidity;
    public String weatherType;
    public String description;
    public String dateTime;

    public WeatherInfo(String cityName, double temperature, int humidity,
                       String weatherType, String description, String dateTime) {
        this.cityName = cityName;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weatherType = weatherType;
        this.description = description;
        this.dateTime = dateTime;
    }
}
