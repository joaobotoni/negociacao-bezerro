package com.omni.negociacaobezerros.di.data.network.gespec;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class GespecInterceptor implements Interceptor {
    private static final String KEY_IP = "ip";
    private static final String KEY_PORT = "port";
    private static final String KEY_USER = "user";
    private static final String HEADER_USER = "X-User-Name";

    private final SharedPreferences preferences;

    public GespecInterceptor(@NonNull SharedPreferences preferences) {
        this.preferences = preferences;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        return chain.proceed(buildRequest(chain.request()));
    }

    private Request buildRequest(@NonNull Request original) {
        return original.newBuilder()
                .url(buildUrl(original))
                .headers(buildHeaders())
                .build();
    }

    private Headers buildHeaders() {
        return new Headers.Builder()
                .add(HEADER_USER, readUser())
                .build();
    }

    private HttpUrl buildUrl(@NonNull Request original) {
        return original.url().newBuilder()
                .host(readHost())
                .port(readPort())
                .build();
    }

    @NonNull
    private String readUser() {
        return requireString(KEY_USER);
    }

    @NonNull
    private String readHost() {
        return requireString(KEY_IP);
    }

    private int readPort() {
        return requireInt(KEY_PORT);
    }

    @NonNull
    private String requireString(@NonNull String key) {
        String value = preferences.getString(key, null);
        if (isBlank(value)) throw new IllegalStateException("Configuração ausente: " + key);
        return value;
    }

    private int requireInt(@NonNull String key) {
        String value = requireString(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Valor inválido para " + key + ": " + value, e);
        }
    }

    private static boolean isBlank(@Nullable String value) {
        return value == null || value.trim().isEmpty();
    }
}