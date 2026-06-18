package com.omni.negociacaobezerros.data.repositories.core;

import androidx.annotation.NonNull;

import com.omni.negociacaobezerros.data.repositories.core.contract.Callback;
import com.omni.negociacaobezerros.data.repositories.core.contract.Operation;
import com.omni.negociacaobezerros.data.repositories.core.contract.Readable;
import com.omni.negociacaobezerros.data.repositories.core.contract.Syncable;
import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;

import java.util.function.Consumer;

import retrofit2.Call;

public abstract class ReadableRepository<E, P> extends AbstractRepository<E> implements Readable<P>, Syncable {
    private final TaskHelper taskHelper;

    protected ReadableRepository(@NonNull AbstractDao<E> dao, @NonNull TaskHelper taskHelper) {
        super(dao);
        this.taskHelper = taskHelper;
    }
    protected abstract Call<P> call();
    protected abstract void save(P data);
    @Override
    public final void pull(Callback<P> callback) {
        source().andThen(push()).apply(null, callback);
    }

    @Override
    public final void sync(Runnable onComplete, Consumer<Throwable> onFailure) {
        pull(Callback.of(data -> onComplete.run(), onFailure));
    }

    private Operation<Void, P> source() {
        return (vd, callback) -> enqueue(call(), callback);
    }

    private Operation<P, P> push() {
        return (data, callback) -> taskHelper.execute(() -> write(data), callback::onResult, callback::onFailure);
    }

    private P write(P data) {
        save(data);
        return data;
    }
}