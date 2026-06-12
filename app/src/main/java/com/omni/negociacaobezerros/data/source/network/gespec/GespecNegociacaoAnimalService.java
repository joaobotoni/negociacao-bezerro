package com.omni.negociacaobezerros.data.source.network.gespec;

import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoAnimal;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface GespecNegociacaoAnimalService {

    @POST("negociacaoAnimal/{usuario}")
    Call<List<NegociacaoAnimal>> insertAll(@Body List<NegociacaoAnimal> negociacoes);

}
