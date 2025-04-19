package com.radioactives.krushimitra.utils;

import android.os.AsyncTask;
import androidx.lifecycle.MutableLiveData;

import com.radioactives.krushimitra.modal.WeatherInfo;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class WeatherRepository {

    private final String apiKey = "62bbd90c1a5fe5bb990073d650a75ea8"; // Replace with your API key

    public void fetchWeather(double lat, double lon, MutableLiveData<WeatherInfo> liveData) {
        new AsyncTask<Void, Void, WeatherInfo>() {
            @Override
            protected WeatherInfo doInBackground(Void... voids) {
                try {
                    String urlString = String.format(
                            "https://api.openweathermap.org/data/2.5/weather?lat=%.4f&lon=%.4f&units=metric&appid=%s",
                            lat, lon, apiKey
                    );

                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");

                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(conn.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(response.toString());

                    String cityName = json.getString("name");
                    double temperature = json.getJSONObject("main").getDouble("temp");
                    int humidity = json.getJSONObject("main").getInt("humidity");
                    String weatherType = json.getJSONArray("weather").getJSONObject(0).getString("main");
                    String description = json.getJSONArray("weather").getJSONObject(0).getString("description");

                    long timestamp = json.getLong("dt");
                    int timezoneOffset = json.getInt("timezone");

                    Instant instant = Instant.ofEpochSecond(timestamp + timezoneOffset);
                    String dateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                            .withZone(ZoneId.of("UTC"))
                            .format(instant);

                    return new WeatherInfo(cityName, temperature, humidity, weatherType, description, dateTime);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(WeatherInfo weatherInfo) {
                liveData.postValue(weatherInfo);
            }
        }.execute();
    }
}
