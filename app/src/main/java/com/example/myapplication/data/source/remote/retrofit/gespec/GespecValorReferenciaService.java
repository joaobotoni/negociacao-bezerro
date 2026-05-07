package com.example.myapplication.data.source.remote.retrofit.gespec;


import com.example.myapplication.data.source.local.entities.ValorReferencia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GespecValorReferenciaService {

    @GET("valoresReferencia")
    Call<List<ValorReferencia>> getAll();
}
