package com.omni.negociacaobezerros.data.source.remote.gespec;

import com.omni.negociacaobezerros.data.source.local.entities.CapacidadeFrete;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecCapacidadeFreteService {
    @GET("capacidadesFrete/{usuario}")
    Call<List<CapacidadeFrete>> getAll(@Path("usuario") String usuario);
}
