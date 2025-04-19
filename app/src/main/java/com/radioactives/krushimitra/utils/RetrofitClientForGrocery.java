package com.radioactives.krushimitra.utils;

import com.radioactives.krushimitra.interfaces.GroceryApiService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientForGrocery {
    private static final String BASE_URL = "https://agrigenius-v1.onrender.com/";
    private static Retrofit retrofit;

    public static GroceryApiService getGroceryApiService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(GroceryApiService.class);
    }
}
