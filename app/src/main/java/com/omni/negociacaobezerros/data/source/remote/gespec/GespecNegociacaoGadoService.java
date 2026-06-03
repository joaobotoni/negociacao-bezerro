package com.omni.negociacaobezerros.data.source.remote.gespec;

import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GespecNegociacaoGadoService {

    @POST("negociacaoGado/{usuario}")
    Call<List<NegociacaoGado>> insertAll(@Body List<NegociacaoGado> negociacoes);
}
