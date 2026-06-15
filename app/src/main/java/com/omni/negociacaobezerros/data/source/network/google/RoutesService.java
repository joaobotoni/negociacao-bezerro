package com.omni.negociacaobezerros.data.source.network.google;

import com.omni.negociacaobezerros.data.source.network.models.NetworkRoutes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RoutesService {
    @POST("directions/v2:computeRoutes")
    Call<NetworkRoutes.Response> computeRoutes(@Body NetworkRoutes.Request request);
}
