package com.omni.negociacaobezerros.data.source.network.gespec;


import com.omni.negociacaobezerros.data.source.local.entities.CategoriaNegociacao;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecCategoriaNegociacaoService {

    @GET("categoriasNeg/{usuario}")
    Call<List<CategoriaNegociacao>> getAll(@Path("usuario") String usuario);
}
