package com.omni.negociacaobezerros.data.repositories.core.contract;

import java.util.function.Consumer;

public interface Syncable {
    void sync(Runnable onComplete, Consumer<Throwable> onFailure);
}

