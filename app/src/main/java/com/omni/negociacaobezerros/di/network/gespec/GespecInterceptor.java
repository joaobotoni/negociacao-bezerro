package com.omni.negociacaobezerros.di.network.gespec;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

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
    private final String ip;
    private final int port;
    private final String user;

    public GespecInterceptor(SharedPreferences preferences) {
        this.ip = readHost(preferences);
        this.port = readPort(preferences);
        this.user = readUser(preferences);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = request(chain.request());
        return chain.proceed(request);
    }

    private Request request(Request request) {
        return request.newBuilder()
                .url(newUrl(request))
                .headers(headers())
                .build();
    }

    private Headers headers() {
        return new Headers.Builder()
                .add("X-User-Name", user)
                .build();
    }

    private HttpUrl newUrl(@NonNull Request original) {
        return original.url().newBuilder()
                .host(ip)
                .port(port)
                .build();
    }

    @NonNull
    private static String readUser(@NonNull SharedPreferences prefs) {
        return requireString(prefs, KEY_USER);
    }

    @NonNull
    private static String readHost(@NonNull SharedPreferences prefs) {
        return requireString(prefs, KEY_IP);
    }

    private static int readPort(@NonNull SharedPreferences prefs) {
        return requireInt(prefs, KEY_PORT);
    }

    @NonNull
    private static String requireString(@NonNull SharedPreferences prefs, @NonNull String key) {
        String value = prefs.getString(key, null);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Configuração ausente: " + key);
        }
        return value;
    }

    private static int requireInt(@NonNull SharedPreferences prefs, @NonNull String key) {
        String value = requireString(prefs, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Valor inválido para " + key + ": " + value, e);
        }
    }
}
