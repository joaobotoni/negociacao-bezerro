package com.omni.negociacaobezerros.di.network;

import com.google.gson.Gson;

import java.util.Locale;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitProvider {
    private final Retrofit retrofit;
    @AssistedInject
    public RetrofitProvider(@Assisted("address") String address, @Assisted("port") String port) {
        final String URL = "http://%s:%s/gespec/gespecservices/NegGadoService/";
        this.retrofit = new Retrofit.Builder()
                .baseUrl(String.format(Locale.getDefault(), URL, address, port))
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
