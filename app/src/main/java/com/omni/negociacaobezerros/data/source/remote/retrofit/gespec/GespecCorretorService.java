package com.omni.negociacaobezerros.data.source.remote.retrofit.gespec;


import com.omni.negociacaobezerros.data.source.local.entities.Corretor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecCorretorService {

    @GET("corretores/{usuario}")
    Call<List<Corretor>> getAll(@Path("usuario") String usuario);
}
