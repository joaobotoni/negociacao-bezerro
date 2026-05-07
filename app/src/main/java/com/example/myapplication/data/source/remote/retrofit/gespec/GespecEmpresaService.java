package com.example.myapplication.data.source.remote.retrofit.gespec;


import com.example.myapplication.data.source.local.entities.Empresa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GespecEmpresaService {

    @GET("empresas")
    Call<List<Empresa>> getAll();
}
