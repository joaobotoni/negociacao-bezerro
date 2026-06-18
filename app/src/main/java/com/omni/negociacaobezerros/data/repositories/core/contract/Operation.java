package com.omni.negociacaobezerros.data.repositories.core.contract;

public interface Operation<I, O> {
    void apply(I i, Callback<O> callback);

    default <R> Operation<I, R> andThen(Operation<O, R> next) {
        return (i, callback) -> apply(i, new Callback<>() {
            @Override
            public void onResult(O data) {
                next.apply(data, callback);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }
}
