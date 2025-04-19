package com.radioactives.krushimitra.interfaces;

import com.radioactives.krushimitra.modal.GroceryItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface GroceryApiService {

    @GET("ranjit485/krushiMitra/refs/heads/master/tomato.json")
    Call<List<GroceryItem>> getTomatoes();

    @GET("ranjit485/krushiMitra/refs/heads/master/potato.json")
    Call<List<GroceryItem>> getPotatoes();

    @GET("ranjit485/krushiMitra/refs/heads/master/onion.json")
    Call<List<GroceryItem>> getOnion();

    @GET("ranjit485/krushiMitra/refs/heads/master/carrot.json")
    Call<List<GroceryItem>> getCarrot();
}
