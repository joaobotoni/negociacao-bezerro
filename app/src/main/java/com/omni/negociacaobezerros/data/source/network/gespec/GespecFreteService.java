package com.omni.negociacaobezerros.data.source.network.gespec;

import com.omni.negociacaobezerros.data.source.local.entities.Frete;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecFreteService {

    @GET("fretes/{usuario}")
    Call<List<Frete>> getAll(@Path("usuario") String usuario);
}
