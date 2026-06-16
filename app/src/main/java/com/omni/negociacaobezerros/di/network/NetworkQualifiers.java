package com.omni.negociacaobezerros.di.network;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;

public class NetworkQualifiers {
    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GespecNetwork {}

    @Qualifier
    @Retention(RetentionPolicy.RUNTIME)
    public @interface GoogleNetwork {}
}
