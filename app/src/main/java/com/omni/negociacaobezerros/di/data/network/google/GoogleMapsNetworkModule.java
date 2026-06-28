package com.omni.negociacaobezerros.di.data.network.google;

import com.omni.negociacaobezerros.di.data.network.HttpClientFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class GoogleMapsNetworkModule {
    private static final String BASE_URL = "https://routes.googleapis.com/";
    @Provides
    @Singleton
    public GoogleMapsInterceptor provideGoogleMapsInterceptor(GoogleMapsApiKeyProvider apiKeyProvider) {
        return new GoogleMapsInterceptor(apiKeyProvider);
    }

    @Provides
    @Singleton
    @GoogleMaps
    public OkHttpClient provideGoogleMapsHttpClient(HttpClientFactory factory, GoogleMapsInterceptor interceptor) {
        return factory.newInstance().addInterceptor(interceptor).build();
    }

    @Provides
    @Singleton
    @GoogleMaps
    public Retrofit provideGoogleMapsRetrofit(@GoogleMaps OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
