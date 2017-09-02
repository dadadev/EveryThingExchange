package com.dadabit.everythingexchange.utils.geocode;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface GoogleMapsClient {
    @GET("/maps/api/geocode/json")
    Call<GmapLocation> getLocation(
            @Query("latlng") String coordinates,
            @Query("result_type") String resultType,
            @Query("key") String apiKey);
}
