package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.repositories.core.contract.Syncable;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import javax.inject.Inject;

public class SynchronizationRepository {
    private final List<Syncable> syncables;
    @Inject
    public SynchronizationRepository(List<Syncable> syncables) {
        this.syncables = syncables;
    }
    public void synchronize(Runnable onComplete, Consumer<Throwable> onFailure){
        AtomicInteger restantes = new AtomicInteger(syncables.size());
        AtomicBoolean falhou = new AtomicBoolean(false);
        for (Syncable syncable : syncables) {
            syncable.sync(
                    () -> onSyncableConcluido(restantes, falhou, onComplete),
                    erro -> onSyncableFalhou(falhou, onFailure, erro));
        }
    }

    private void onSyncableConcluido(AtomicInteger restantes, AtomicBoolean falhou, Runnable onComplete) {
        if (falhou.get()) return;
        if (restantes.decrementAndGet() == 0) onComplete.run();
    }

    private void onSyncableFalhou(AtomicBoolean falhou, Consumer<Throwable> onFailure, Throwable erro) {
        if (falhou.compareAndSet(false, true)) onFailure.accept(erro);
    }
}
