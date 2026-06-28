package com.omni.negociacaobezerros.di.data.network.gespec;


import android.content.SharedPreferences;

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
public class GespecNetworkModule {
    private static final String BASE_URL = "http://placeholder.com/gespec/gespecservices/NegGadoService/";
    @Provides
    @Singleton
    public GespecInterceptor providerGespecInterceptor(SharedPreferences sharedPreferences) {
        return new GespecInterceptor(sharedPreferences);
    }

    @Provides
    @Singleton
    @Gespec
    public OkHttpClient provideGespecHttpClient(HttpClientFactory factory, GespecInterceptor interceptor) {
        return factory.newInstance().addInterceptor(interceptor).build();
    }

    @Provides
    @Singleton
    @Gespec
    public Retrofit provideGespecRetrofit(@Gespec OkHttpClient okHttpClient) {
        return new Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
