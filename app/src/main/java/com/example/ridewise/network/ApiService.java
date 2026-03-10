package com.example.ridewise.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("distancematrix/json")
    Call<Object> getDistance(
            @Query("origins") String origins,
            @Query("destinations") String destinations,
            @Query("key") String apiKey
    );
}
