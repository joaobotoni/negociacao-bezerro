package com.omni.negociacaobezerros.data.repositories.core.contract;

public interface Writable<W> extends Syncable {
    void push(W data, Callback<W> callback);
}
