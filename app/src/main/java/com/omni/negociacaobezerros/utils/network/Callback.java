package com.omni.negociacaobezerros.utils.network;

import androidx.annotation.NonNull;

import retrofit2.Call;
import retrofit2.Response;

public abstract class Callback<T> implements retrofit2.Callback<T> {
    private static final int CODE_OFFLINE = 0;
    private static final String MSG_HTTP = "Erro HTTP ";
    private static final String MSG_OFFLINE = "Sem conexão";
    public abstract void onSuccess(@NonNull T body);
    public abstract void onError(int code, @NonNull String mensagem, Throwable t);

    @Override
    public void onResponse(@NonNull Call<T> call, @NonNull Response<T> response) {
        if (response.isSuccessful() && response.body() != null) {
            onSuccess(response.body());
        } else {
            onError(response.code(), MSG_HTTP + response.code(), null);
        }
    }

    @Override
    public void onFailure(@NonNull Call<T> call, @NonNull Throwable t) {
        onError(CODE_OFFLINE, t.getMessage() != null ? t.getMessage() : MSG_OFFLINE, t);
    }
}