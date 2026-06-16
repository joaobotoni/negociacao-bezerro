package com.omni.negociacaobezerros.di.network.gespec;

import androidx.annotation.NonNull;

import com.google.gson.GsonBuilder;
import com.omni.negociacaobezerros.BuildConfig;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCapacidadeFreteService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaFreteService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaNegociacaoService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCorretorService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecEmpresaService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecFreteService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecNegociacaoAnimalService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecNegociacaoGadoService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecRacasService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoReferenciaService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoVeiculoFreteService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecValorReferenciaService;
import com.omni.negociacaobezerros.di.network.NetworkQualifiers.GespecNetwork;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class GespecNetworkModule {
    private static final String PLACEHOLDER_BASE_URL = "http://localhost/gespec/gespecservices/NegGadoService/";
    private static final int TIMEOUT_SECONDS = 30;

    @Provides
    @Singleton
    @GespecNetwork
    public OkHttpClient provideOkHttpClient(@NonNull GespecInterceptor configInterceptor) {
        return clientBuilder().addInterceptor(configInterceptor).addInterceptor(logging()).build();
    }

    @Provides
    @Singleton
    @GespecNetwork
    public Retrofit provideRetrofit(@NonNull @GespecNetwork OkHttpClient client) {
        return retrofitBuilder(client).build();
    }

    @Provides
    public GespecCapacidadeFreteService provideCapacidadeFreteService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecCapacidadeFreteService.class);
    }

    @Provides
    public GespecCategoriaFreteService provideCategoriaFreteService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecCategoriaFreteService.class);
    }

    @Provides
    public GespecCategoriaNegociacaoService provideCategoriaNegociacaoService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecCategoriaNegociacaoService.class);
    }

    @Provides
    public GespecCorretorService provideCorretorService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecCorretorService.class);
    }

    @Provides
    public GespecEmpresaService provideEmpresaService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecEmpresaService.class);
    }

    @Provides
    public GespecFreteService provideFreteService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecFreteService.class);
    }

    @Provides
    public GespecRacasService provideRacasService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecRacasService.class);
    }

    @Provides
    public GespecTipoReferenciaService provideTipoReferenciaService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecTipoReferenciaService.class);
    }

    @Provides
    public GespecTipoVeiculoFreteService provideTipoVeiculoFreteService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecTipoVeiculoFreteService.class);
    }

    @Provides
    public GespecValorReferenciaService provideValorReferenciaService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecValorReferenciaService.class);
    }

    @Provides
    public GespecNegociacaoAnimalService provideNegociacaoAnimalService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecNegociacaoAnimalService.class);
    }

    @Provides
    public GespecNegociacaoGadoService provideNegociacaoGadoService(@NonNull @GespecNetwork Retrofit retrofit) {
        return retrofit.create(GespecNegociacaoGadoService.class);
    }

    @NonNull
    private static OkHttpClient.Builder clientBuilder() {
        return new OkHttpClient.Builder()
                .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    @NonNull
    private static Retrofit.Builder retrofitBuilder(@NonNull OkHttpClient client) {
        return new Retrofit.Builder().baseUrl(PLACEHOLDER_BASE_URL).client(client)
                .addConverterFactory(gsonConverter());
    }

    @NonNull
    private static HttpLoggingInterceptor logging() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(loggingLevel());
        return interceptor;
    }

    @NonNull
    private static HttpLoggingInterceptor.Level loggingLevel() {
        return BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE;
    }

    @NonNull
    private static Converter.Factory gsonConverter() {
        return GsonConverterFactory.create(new GsonBuilder().create());
    }
}