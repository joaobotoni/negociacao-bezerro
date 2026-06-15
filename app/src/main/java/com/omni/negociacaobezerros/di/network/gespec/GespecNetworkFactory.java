package com.omni.negociacaobezerros.di.network.gespec;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.omni.negociacaobezerros.BuildConfig;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;
import dagger.assisted.AssistedInject;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public final class GespecNetworkFactory {
    private final Retrofit retrofit;
    private static final String URL_TEMPLATE = "http://%s:%s/gespec/gespecservices/NegGadoService/";
    private static final int TIMEOUT_SECONDS = 30;

    @AssistedFactory
    public interface Factory {
        GespecNetworkFactory create(
                @Assisted("address") String address,
                @Assisted("port") String port,
                @Assisted("username") String username
        );
    }

    @AssistedInject
    public GespecNetworkFactory(
            @Assisted("address") String address,
            @Assisted("port") String port,
            @Assisted("username") String username
    ) {
        this.retrofit = newRetrofit(address, port, username);
    }

    @NonNull
    public Retrofit retrofit() {
        return retrofit;
    }

    @NonNull
    private static Retrofit newRetrofit(@NonNull String address, @NonNull String port, @NonNull String username) {
        return new Retrofit.Builder()
                .baseUrl(newBaseUrl(address, port))
                .client(newOkHttpClient(username))
                .addConverterFactory(GsonConverterFactory.create(newGson()))
                .build();
    }

    @NonNull
    private static String newBaseUrl(@NonNull String address, @NonNull String port) {
        return String.format(Locale.US, URL_TEMPLATE, address, port);
    }

    @NonNull
    private static OkHttpClient newOkHttpClient(@NonNull String username) {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(newLoggingInterceptor())
                .addInterceptor(createUserNameInterceptor(username))
                .build();
    }

    private static Interceptor createUserNameInterceptor(@NonNull String userName) {
        return chain -> proceedWithUserName(chain, userName);
    }

    private static Response proceedWithUserName(Interceptor.Chain chain, String userName) throws IOException {
        return chain.proceed(requestWithUserName(chain, userName));
    }

    private static Request requestWithUserName(Interceptor.Chain chain, String userName) {
        return chain.request().newBuilder()
                .addHeader("X-User-Name", userName)
                .build();
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