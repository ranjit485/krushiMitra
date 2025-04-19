package com.radioactives.krushimitra.fragments;

import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.adapters.ForecastAdapter;
import com.radioactives.krushimitra.interfaces.WeatherApiService;
import com.radioactives.krushimitra.modal.ForecastItem;
import com.radioactives.krushimitra.modal.WeatherResponse;
import com.radioactives.krushimitra.utils.RetrofitClient;
import com.radioactives.krushimitra.view.WeatherViewModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private double latitude = 16.8544; // fallback Sangli
    private double longitude = 74.5648;
    private FusedLocationProviderClient fusedLocationClient;

    RecyclerView forecastRecycler;
    ViewModel viewModel;
    private WeatherViewModel weatherViewModel;
    private TextView weatherDescriptionTextView,cityNameTextView,dateAndTimeTextView,tempratureTextView;
    private ImageView wetherIcon;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        cityNameTextView = view.findViewById(R.id.city_name);
        weatherDescriptionTextView = view.findViewById(R.id.weather_description);
        dateAndTimeTextView = view.findViewById(R.id.date_time);
        tempratureTextView = view.findViewById(R.id.temperature);

        wetherIcon = view.findViewById(R.id.weather_icon);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        requestLocation();

        if (getActivity() != null) {
            // This line changes the title of the MaterialToolbar from the Activity
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Weather Today");
        }
        forecastRecycler = view.findViewById(R.id.forecast_recycler);
        forecastRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));


//        // Dummy data example
//        List<ForecastItem> forecastList = new ArrayList<>();
//        forecastList.add(new ForecastItem("03:00 PM", "31", "01d")); // 01d = sunny icon
//        forecastList.add(new ForecastItem("06:00 PM", "29", "04d"));
//        forecastList.add(new ForecastItem("09:00 PM", "26", "10n"));
//
//        ForecastAdapter adapter = new ForecastAdapter(getContext(), forecastList);
//        forecastRecycler.setAdapter(adapter);
        fetchForecast();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        weatherViewModel.getWeatherLiveData().observe(getViewLifecycleOwner(), weatherInfo -> {
            if (weatherInfo != null) {
                String display = "City: " + weatherInfo.cityName +
                        "\nTemperature: " + weatherInfo.temperature + "°C" +
                        "\nHumidity: " + weatherInfo.humidity + "%" +
                        "\nWeather: " + weatherInfo.weatherType +
                        "\nDescription: " + weatherInfo.description +
                        "\nDate & Time: " + weatherInfo.dateTime;
                cityNameTextView.setText(weatherInfo.cityName);
                dateAndTimeTextView.setText(weatherInfo.dateTime);
                weatherDescriptionTextView.setText(weatherInfo.description);

                String temp = String.valueOf(weatherInfo.temperature);
                tempratureTextView.setText(temp+"°C");
            } else {
                weatherDescriptionTextView.setText("Failed to fetch weather");
            }
        });

        // Load weather using latitude and longitude
        weatherViewModel.loadWeather(latitude, longitude);
    }
    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    this.latitude = location.getLatitude();
                    this.longitude = location.getLongitude();
                }
                fetchForecast();
            });
        }
    }

    private void fetchForecast() {
        String city = "Sangli"; // or dynamic input
        String apiKey = "62bbd90c1a5fe5bb990073d650a75ea8";
        String units = "metric";

        WeatherApiService api = RetrofitClient.getApiService();
        Call<WeatherResponse> call = api.getForecast(city, apiKey, units);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    List<WeatherResponse.WeatherItem> items = response.body().list;

                    List<ForecastItem> forecastList = new ArrayList<>();

                    // Show next 8 entries (~24 hours)
                    for (int i = 0; i < 8; i++) {
                        WeatherResponse.WeatherItem item = items.get(i);

                        String time = item.dtTxt.split(" ")[1].substring(0, 5); // "15:00"
                        String temp = String.valueOf((int) item.main.temp);
                        String icon = item.weather.get(0).icon;

                        forecastList.add(new ForecastItem(time, temp, icon));
                    }

                    ForecastAdapter adapter = new ForecastAdapter(getContext(), forecastList);
                    forecastRecycler.setAdapter(adapter);
                } else {
                    Toast.makeText(getContext(), "Failed to get weather", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        } else {
            Toast.makeText(getContext(), "Location permission denied. Using default location.", Toast.LENGTH_SHORT).show();
        }
    }

}