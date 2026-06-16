package com.omni.negociacaobezerros.di.network.google;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.omni.negociacaobezerros.BuildConfig;
import com.omni.negociacaobezerros.data.source.network.google.RoutesService;
import com.omni.negociacaobezerros.di.network.NetworkQualifiers.GoogleNetwork;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class RoutesNetworkModule {
    private static final String BASE_URL = "https://routes.googleapis.com/";
    private static final String FIELD_MASK = "routes.distanceMeters";
    private static final String HEADER_API_KEY = "X-Goog-Api-Key";
    private static final String HEADER_FIELD_MASK = "X-Goog-FieldMask";
    private static final int TIMEOUT_SECONDS = 30;

    @Provides
    @Singleton
    @GoogleNetwork
    public OkHttpClient provideOkHttpClient(@NonNull GoogleMapsApiKeyProvider apiKeyProvider) {
        return clientBuilder()
                .addInterceptor(googleHeaders(apiKeyProvider.apiKey()))
                .addInterceptor(logging())
                .build();
    }

    @Provides
    @Singleton
    @GoogleNetwork // <-- Adicionado aqui
    public Retrofit provideRetrofit(@NonNull @GoogleNetwork OkHttpClient client) {
        return retrofitBuilder(client).build();
    }

    @Provides
    @Singleton
    public RoutesService provideRoutesService(@NonNull @GoogleNetwork Retrofit retrofit) {
        return retrofit.create(RoutesService.class);
    }

    @NonNull
    private static OkHttpClient.Builder clientBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @NonNull
    private static Retrofit.Builder retrofitBuilder(@NonNull OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(gsonConverter());
    }

    @NonNull
    private static Interceptor googleHeaders(@NonNull String apiKey) {
        return chain -> chain.proceed(withGoogleHeaders(chain.request(), apiKey));
    }

    @NonNull
    private static Request withGoogleHeaders(@NonNull Request request, @NonNull String apiKey) {
        return request.newBuilder()
                .addHeader(HEADER_API_KEY, apiKey)
                .addHeader(HEADER_FIELD_MASK, FIELD_MASK)
                .build();
    }

    @NonNull
    private static HttpLoggingInterceptor logging() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(loggingLevel());
        return interceptor;
    }

    @NonNull
    private static HttpLoggingInterceptor.Level loggingLevel() {
        return BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
    }

    @NonNull
    private static Converter.Factory gsonConverter() {
        return GsonConverterFactory.create(new GsonBuilder().create());
    }
}