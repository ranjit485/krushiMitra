package com.radioactives.krushimitra.interfaces;

import com.radioactives.krushimitra.modal.GroceryItem;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("grocery/items") // Replace with your API endpoint
    Call<Void> submitGroceryItem(@Body GroceryItem groceryItem);
}
