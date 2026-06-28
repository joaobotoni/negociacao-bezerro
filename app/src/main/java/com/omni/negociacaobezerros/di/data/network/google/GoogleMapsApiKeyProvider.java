package com.omni.negociacaobezerros.di.data.network.google;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.omni.negociacaobezerros.R;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class GoogleMapsApiKeyProvider {
    private final String apiKey;
    @Inject
    public GoogleMapsApiKeyProvider(@ApplicationContext @NonNull Context context) {
        this.apiKey = resolve(context);
    }

    @NonNull
    public String apiKey() {
        return apiKey;
    }

    @NonNull
    private static String resolve(@NonNull Context ctx) {
        String key = read(ctx);
        validate(key, ctx);
        return key;
    }

    private static String read(@NonNull Context ctx) {
        try {
            return value(metaData(ctx), keyName(ctx));
        } catch (PackageManager.NameNotFoundException e) {
            throw notFound(ctx, e);
        }
    }

    @Nullable
    private static String value(@Nullable Bundle metaData, @NonNull String name) {
        return metaData == null ? null : metaData.getString(name);
    }

    @Nullable
    private static Bundle metaData(@NonNull Context ctx) throws PackageManager.NameNotFoundException {
        return appInfo(ctx).metaData;
    }

    @NonNull
    private static ApplicationInfo appInfo(@NonNull Context ctx) throws PackageManager.NameNotFoundException {
        return ctx.getPackageManager().getApplicationInfo(ctx.getPackageName(), PackageManager.GET_META_DATA);
    }

    @NonNull
    private static String keyName(@NonNull Context ctx) {
        return ctx.getString(R.string.package_chave_api_google_maps);
    }

    private static void validate(@Nullable String key, @NonNull Context ctx) {
        if (!isFilled(key)) throw missing(ctx);
    }

    private static boolean isFilled(@Nullable String value) {
        return value != null && !value.trim().isEmpty();
    }

    @NonNull
    private static IllegalStateException missing(@NonNull Context ctx) {
        return new IllegalStateException(errorMsg(ctx));
    }

    @NonNull
    private static IllegalStateException notFound(@NonNull Context ctx, @NonNull Exception cause) {
        return new IllegalStateException(errorMsg(ctx), cause);
    }

    @NonNull
    private static String errorMsg(@NonNull Context ctx) {
        return ctx.getString(R.string.erro_chave_api_ausente);
    }
}