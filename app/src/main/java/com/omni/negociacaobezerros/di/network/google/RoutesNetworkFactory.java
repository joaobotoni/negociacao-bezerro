package com.omni.negociacaobezerros.di.network.google;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omni.negociacaobezerros.BuildConfig;

import java.util.concurrent.TimeUnit;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class RoutesNetworkFactory {
    private static final String BASE_URL = "https://routes.googleapis.com/";
    private static final String FIELD_MASK = "routes.distanceMeters";
    private static final int TIMEOUT_SECONDS = 30;
    private final Retrofit retrofit;

    @AssistedFactory
    public interface Factory {
        RoutesNetworkFactory create(@Assisted("apiKey") String apiKey);
    }

    @AssistedInject
    public RoutesNetworkFactory(@Assisted("apiKey") String apiKey) {
        this.retrofit = newRetrofit(apiKey);
    }

    @NonNull
    public Retrofit retrofit() {
        return retrofit;
    }

    @NonNull
    private static Retrofit newRetrofit(@NonNull String apiKey) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(newOkHttpClient(apiKey))
                .addConverterFactory(GsonConverterFactory.create(newGson()))
                .build();
    }

    @NonNull
    private static OkHttpClient newOkHttpClient(@NonNull String apiKey) {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(newApiKeyInterceptor(apiKey))
                .addInterceptor(newFieldMaskInterceptor())
                .addInterceptor(newLoggingInterceptor())
                .build();
    }

    @NonNull
    private static Interceptor newApiKeyInterceptor(@NonNull String apiKey) {
        return chain -> chain.proceed(requestWithHeader(chain, "X-Goog-Api-Key", apiKey));
    }

    @NonNull
    private static Interceptor newFieldMaskInterceptor() {
        return chain -> chain.proceed(requestWithHeader(chain, "X-Goog-FieldMask", FIELD_MASK));
    }

    @NonNull
    private static Request requestWithHeader(@NonNull Interceptor.Chain chain, @NonNull String name, @NonNull String value) {
        return chain.request().newBuilder().addHeader(name, value).build();
    }

    @NonNull
    private static HttpLoggingInterceptor newLoggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(loggingLevel());
        return interceptor;
    }

    private static HttpLoggingInterceptor.Level loggingLevel() {
        return BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
    }

    @NonNull
    private static Gson newGson() {
        return new GsonBuilder().create();
    }
}