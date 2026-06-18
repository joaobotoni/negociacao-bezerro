
package com.omni.negociacaobezerros.data.repositories.core.contract;

import java.util.function.Consumer;

public interface Callback<T> {
    void onResult(T data);
    void onFailure(Throwable error);

    static <T> Callback<T> of(Consumer<T> onResult, Consumer<Throwable> onFailure) {
        return new Callback<>() {
            @Override public void onResult(T data) { onResult.accept(data); }
            @Override public void onFailure(Throwable error) { onFailure.accept(error); }
        };
    }
}