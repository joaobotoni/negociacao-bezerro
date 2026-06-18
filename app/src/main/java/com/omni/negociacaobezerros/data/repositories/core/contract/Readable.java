package com.omni.negociacaobezerros.data.repositories.core.contract;

public interface Readable<R> extends Syncable {
    void pull(Callback<R> callback);
}
