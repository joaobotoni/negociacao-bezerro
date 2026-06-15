package com.omni.negociacaobezerros.di.network.google;
import com.omni.negociacaobezerros.data.source.network.google.RoutesService;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RoutesNetworkModule {
    @Provides
    public RoutesService provideRoutesService(RoutesNetworkManager manager) {
        return manager.createService(RoutesService.class);
    }
}
