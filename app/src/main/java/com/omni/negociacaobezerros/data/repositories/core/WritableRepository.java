package com.omni.negociacaobezerros.data.repositories.core;


import com.omni.negociacaobezerros.data.repositories.core.contract.Callback;
import com.omni.negociacaobezerros.data.repositories.core.contract.Operation;
import com.omni.negociacaobezerros.data.repositories.core.contract.Syncable;
import com.omni.negociacaobezerros.data.repositories.core.contract.Writable;
import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;

import java.util.function.Consumer;

import retrofit2.Call;

public abstract class WritableRepository<E, P> extends AbstractRepository<E> implements Writable<P>, Syncable {
    protected WritableRepository(AbstractDao<E> dao) {
        super(dao);
    }

    protected abstract Call<P> call(P data);

    protected abstract P load();

    @Override
    public final void sync(Runnable onComplete, Consumer<Throwable> onFailure) {
        push(load(), Callback.of(d -> onComplete.run(), onFailure));
    }

    @Override
    public final void push(P data, Callback<P> callback) {
        source().apply(data, callback);
    }

    private Operation<P, P> source() {
        return (data, callback) -> enqueue(call(data), callback);
    }
}