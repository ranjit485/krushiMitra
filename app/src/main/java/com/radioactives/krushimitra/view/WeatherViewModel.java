package com.radioactives.krushimitra.view;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.radioactives.krushimitra.modal.WeatherInfo;
import com.radioactives.krushimitra.utils.WeatherRepository;

public class WeatherViewModel extends ViewModel {

    private final WeatherRepository repository = new WeatherRepository();
    private final MutableLiveData<WeatherInfo> weatherLiveData = new MutableLiveData<>();

    public LiveData<WeatherInfo> getWeatherLiveData() {
        return weatherLiveData;
    }

    public void loadWeather(double lat, double lon) {
        repository.fetchWeather(lat, lon, weatherLiveData);
    }
}
