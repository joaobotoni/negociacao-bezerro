package com.omni.negociacaobezerros.data.repositories.core;

import androidx.annotation.NonNull;

import com.omni.negociacaobezerros.data.repositories.core.contract.Callback;
import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;


import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public abstract class AbstractRepository<T> {
    private final AbstractDao<T> dao;
    protected AbstractRepository(AbstractDao<T> dao) {
        this.dao = dao;
    }

    public List<T> getAll() {
        return dao.getAll();
    }

    public long insert(T t) {
        return dao.insert(t);
    }

    public void insertAll(List<T> list) {
        dao.insertAll(list);
    }

    public int update(T t) {
        return dao.update(t);
    }

    public int delete(T t) {
        return dao.delete(t);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    protected final <X> void enqueue(@NonNull Call<X> call, @NonNull Callback<X> callback) {
        call.enqueue(new retrofit2.Callback<>() {
            @Override
            public void onResponse(@NonNull Call<X> c, @NonNull Response<X> response) {
                if (!response.isSuccessful()) {
                    callback.onFailure(new IOException("HTTP " + response.code()));
                    return;
                }
                X body = response.body();
                if (body == null) {
                    callback.onFailure(new IOException("Empty body on HTTP " + response.code()));
                    return;
                }
                callback.onResult(body);
            }

            @Override
            public void onFailure(@NonNull Call<X> c, @NonNull Throwable t) {
                callback.onFailure(t);
            }
        });
    }
}
