package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.repositories.core.contract.Syncable;

import java.util.List;
import java.util.function.Consumer;
import javax.inject.Inject;

public class SynchronizationRepository {
    private final List<Syncable> syncables;
    @Inject
    public SynchronizationRepository(List<Syncable> syncables) {
        this.syncables = syncables;
    }
    public void synchronize(Runnable onComplete, Consumer<Throwable> onFailure){
         for(Syncable syncable : syncables){
             syncable.sync(onComplete, onFailure);
         }
    }
}
