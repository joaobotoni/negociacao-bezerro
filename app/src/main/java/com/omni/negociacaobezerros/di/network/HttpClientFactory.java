package com.omni.negociacaobezerros.di.network;


import javax.inject.Singleton;

import okhttp3.OkHttpClient;

@Singleton
public class HttpClientFactory {
    private volatile OkHttpClient okHttpClient;
    private OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (this) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient();
                }
            }
        }
        return okHttpClient;
    }

    public OkHttpClient.Builder newInstance() {
        return getOkHttpClient().newBuilder();
    }
}