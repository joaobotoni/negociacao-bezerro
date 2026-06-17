package com.omni.negociacaobezerros.di.network.google;

import com.omni.negociacaobezerros.data.source.network.google.RoutesService;

import org.jspecify.annotations.NonNull;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

@Module
@InstallIn(SingletonComponent.class)
public class GoogleMapsServiceModule {
    @Provides
    @Singleton
    public RoutesService provideRoutesService(@NonNull @GoogleMaps Retrofit retrofit) {
        return retrofit.create(RoutesService.class);
    }
}
