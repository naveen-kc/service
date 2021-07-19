package com.ssht.successofdreams;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

  // @FormUrlEncoded
    @GET("get-locations")
    Call<LocationResponse> getLocations(@Query ("token") String token);
}
