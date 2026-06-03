package com.omni.negociacaobezerros.di.network;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface RetrofitProviderFactory {
    RetrofitProvider create(@Assisted("address") String address, @Assisted("port") String port);
}
