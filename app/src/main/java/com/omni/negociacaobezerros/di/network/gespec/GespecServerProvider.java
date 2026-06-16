package com.omni.negociacaobezerros.di.network.gespec;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class GespecServerProvider {
    private static final String PREFS_NAME = "GESPEC_NETWORK_PREFS";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_PORT = "port";
    private static final String KEY_USERNAME = "username";
    private final SharedPreferences prefs;

    @Inject
    public GespecServerProvider(@ApplicationContext @NonNull Context context) {
        this.prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void update(@NonNull String address, @NonNull String port, @NonNull String username) {
        prefs.edit().putString(KEY_ADDRESS, address).putString(KEY_PORT, port)
                .putString(KEY_USERNAME, username).apply();
    }

    @Nullable
    public String address() {
        return prefs.getString(KEY_ADDRESS, null);
    }

    @Nullable
    public String port() {
        return prefs.getString(KEY_PORT, null);
    }

    @Nullable
    public String username() {
        return prefs.getString(KEY_USERNAME, null);
    }

    public boolean isInitialized() {
        return isFilled(address()) && isFilled(port()) && isFilled(username());
    }

    private static boolean isFilled(@Nullable String value) {
        return value != null && !value.trim().isEmpty();
    }
}