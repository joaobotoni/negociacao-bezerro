package com.example.myapplication.data.source.remote.retrofit.gespec;

import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface GespecUsuarioService {

    @POST
    Call<String> sync(
            @Url String url,
            @Header("Content-Type") String contentType,
            @Header("DATASOURCE_DEFAULT") String datasource,
            @Header("USUARIO_LOGADO") String usuarioLogado
    );
}
