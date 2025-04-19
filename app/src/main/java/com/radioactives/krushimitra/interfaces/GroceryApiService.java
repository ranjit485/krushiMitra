package com.radioactives.krushimitra.interfaces;

import com.radioactives.krushimitra.modal.GroceryItem;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface GroceryApiService {

    @GET("api/grocery/all")
    Call<List<GroceryItem>> getAllItems();

    @GET("api/grocery/products/{productName}")
    Call<List<GroceryItem>> getItemsByProduct(@Path("productName") String productName);

    @GET("api/grocery/{id}")
    Call<GroceryItem> getItemById(@Path("id") Long id);

    @POST("api/grocery/add")
    Call<GroceryItem> addItem(@Body GroceryItem item);
}
