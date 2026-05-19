package com.omni.negociacaobezerros.ui.helpers;

import android.os.Handler;

import androidx.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;


import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class TaskHelper {
    private final ExecutorService executor;
    private final Handler mainHandler;

    @Inject
    public TaskHelper(@NonNull ExecutorService executor, @NonNull Handler mainHandler) {
        this.executor = executor;
        this.mainHandler = mainHandler;
    }

    public <T> void execute(
            @NonNull Callable<T> task,
            @NonNull Consumer<T> onSuccess,
            @NonNull Consumer<Exception> onError
    ) {
        executor.submit(() -> {
            try {
                T result = task.call();
                mainHandler.post(() -> onSuccess.accept(result));
            } catch (Exception e) {
                mainHandler.post(() -> onError.accept(e));
            }
        });
    }
}
