package com.omni.negociacaobezerros.di.data.network.google;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


public class GoogleMapsInterceptor implements Interceptor {
    private static final String FIELD_MASK = "routes.distanceMeters";
    private final GoogleMapsApiKeyProvider apiKeyProvider;

    public GoogleMapsInterceptor(GoogleMapsApiKeyProvider apiKeyProvider) {
        this.apiKeyProvider = apiKeyProvider;
    }

    @NonNull
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = request(chain.request());
        return chain.proceed(request);
    }

    private Request request(Request request) {
        return request.newBuilder()
                .headers(headers())
                .build();
    }
    private Headers headers() {
        return new Headers.Builder()
                .add("X-Goog-Api-Key", apiKeyProvider.apiKey())
                .add("X-Goog-FieldMask", FIELD_MASK)
                .build();
    }
}
