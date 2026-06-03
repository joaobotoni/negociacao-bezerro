package com.omni.negociacaobezerros.di.network;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import retrofit2.Retrofit;

@Singleton
public class RetrofitManager {
    private final RetrofitProviderFactory factory;
    private Retrofit retrofit;

    @Inject
    public RetrofitManager(RetrofitProviderFactory factory) {
        this.factory = factory;
    }
    public void configure(String address, String port) {
        this.retrofit = factory.create(address, port).getRetrofit();
    }

    public Retrofit getRetrofit() {
        if (retrofit == null) {
            throw new IllegalStateException("Conexão não configurada.");
        }
        return retrofit;
    }
}
