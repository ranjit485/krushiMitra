package com.radioactives.krushimitra.fragments;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.radioactives.krushimitra.R;
import com.radioactives.krushimitra.adapters.ForecastAdapter;
import com.radioactives.krushimitra.interfaces.WeatherApiService;
import com.radioactives.krushimitra.modal.ForecastItem;
import com.radioactives.krushimitra.modal.WeatherResponse;
import com.radioactives.krushimitra.utils.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {

    RecyclerView forecastRecycler;
    ViewModel viewModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_weather, container, false);

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

}