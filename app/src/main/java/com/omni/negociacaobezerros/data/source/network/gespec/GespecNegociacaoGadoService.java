package com.omni.negociacaobezerros.data.source.network.gespec;

import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface GespecNegociacaoGadoService {

    @POST("negociacaoGado")
    Call<List<NegociacaoGado>> insertAll(List<NegociacaoGado> negociacoes);
}
