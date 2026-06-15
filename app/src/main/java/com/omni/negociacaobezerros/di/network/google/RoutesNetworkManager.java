package com.omni.negociacaobezerros.di.network.google;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.omni.negociacaobezerros.R;

import dagger.hilt.android.qualifiers.ApplicationContext;
import jakarta.inject.Inject;
import retrofit2.Retrofit;

public class RoutesNetworkManager {
    private final Retrofit retrofit;
    @Inject
    public RoutesNetworkManager(RoutesNetworkFactory.Factory factory, @ApplicationContext Context context) {
        this.retrofit = factory.create(loadApiKey(context)).retrofit();
    }

    @NonNull
    public <T> T createService(@NonNull Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }

    @NonNull
    private static String loadApiKey(@NonNull Context context) {
        String key = fetchApiKey(context);
        validateApiKey(key, context);
        return key;
    }

    private static String fetchApiKey(@NonNull Context context) {
        try {
            return fetchMetaDataKey(context, fetchAppInfo(context));
        } catch (PackageManager.NameNotFoundException e) {
            throw newApiKeyException(context, e);
        }
    }

    private static ApplicationInfo fetchAppInfo(@NonNull Context context) throws PackageManager.NameNotFoundException {
        return context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
    }

    private static String fetchMetaDataKey(@NonNull Context context, @NonNull ApplicationInfo info) {
        return info.metaData.getString(context.getString(R.string.package_chave_api_google_maps));
    }

    private static void validateApiKey(String key, @NonNull Context context) {
        if (key == null || key.isBlank()) throw newApiKeyException(context, null);
    }

    private static IllegalStateException newApiKeyException(@NonNull Context context, Exception cause) {
        return new IllegalStateException(context.getString(R.string.erro_chave_api_ausente), cause);
    }
}
