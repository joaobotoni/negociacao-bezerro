package com.omni.negociacaobezerros.di.network;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {
    @Provides
    @Singleton
    public HttpClientFactory provideHttpClientFactory() {
        return new HttpClientFactory();
    }
}