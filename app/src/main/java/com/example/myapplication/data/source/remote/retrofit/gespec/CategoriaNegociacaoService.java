package com.example.myapplication.data.source.remote.retrofit.gespec;


import com.example.myapplication.data.source.local.entities.CategoriaNegociacao;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CategoriaNegociacaoService {

    @GET("categoriasNeg")
    Call<List<CategoriaNegociacao>> getAll();
}
