package com.omni.negociacaobezerros.di.local;
import android.content.Context;
import android.location.Geocoder;

import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class LocationModule {
    @Provides
    public Geocoder provideGeocoder(@ApplicationContext Context context) {
        return new Geocoder(context, Locale.getDefault());
    }
}
