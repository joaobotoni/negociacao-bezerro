package com.omni.negociacaobezerros.data.source.network.gespec;


import com.omni.negociacaobezerros.data.source.local.entities.Empresa;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GespecEmpresaService {

    @GET("empresas")
    Call<List<Empresa>> getAll();
}
