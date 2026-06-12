package com.omni.negociacaobezerros.data.source.network.gespec;

import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecCategoriaFreteService {

    @GET("categoriasFrete/{usuario}")
    Call<List<CategoriaFrete>> getAll(@Path("usuario") String usuario);
}
