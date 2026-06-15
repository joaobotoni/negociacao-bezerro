package com.omni.negociacaobezerros.di.network.gespec;

import androidx.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Retrofit;

@Singleton
public class GespecNetworkManager {
    private Retrofit retrofit;
    private final GespecNetworkFactory.Factory factory;

    @Inject
    public GespecNetworkManager(GespecNetworkFactory.Factory factory) {
        this.factory = factory;
    }
    public synchronized void initialize(@NonNull String address, @NonNull String port, @NonNull String username) {
        this.retrofit = factory.create(address, port, username).retrofit();
    }

    public synchronized boolean isNotNetworkReady() {
        return retrofit == null;
    }

    @NonNull
    public synchronized <T> T createService(@NonNull Class<T> serviceClass) {
        if (isNotNetworkReady()) {
            throw networkNotConfiguredException();
        }
        return retrofit.create(serviceClass);
    }

    private static IllegalStateException networkNotConfiguredException() {
        return new IllegalStateException("NetworkManager não configurado. Chame initialize() antes de usar serviços de rede.");
    }
}