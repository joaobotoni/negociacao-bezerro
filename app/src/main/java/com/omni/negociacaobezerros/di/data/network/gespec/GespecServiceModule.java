package com.omni.negociacaobezerros.di.data.network.gespec;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecAcessoService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCapacidadeFreteService;


import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;

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
import com.omni.negociacaobezerros.data.source.network.gespec.GespecUsuarioService;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecValorReferenciaService;

import androidx.annotation.NonNull;

import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class GespecServiceModule {

    @Provides
    @Singleton
    public GespecCapacidadeFreteService provideCapacidadeFreteService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecCapacidadeFreteService.class);
    }

    @Provides
    @Singleton
    public GespecCategoriaFreteService provideCategoriaFreteService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecCategoriaFreteService.class);
    }

    @Provides
    @Singleton
    public GespecCategoriaNegociacaoService provideCategoriaNegociacaoService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecCategoriaNegociacaoService.class);
    }

    @Provides
    @Singleton
    public GespecCorretorService provideCorretorService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecCorretorService.class);
    }

    @Provides
    @Singleton
    public GespecEmpresaService provideEmpresaService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecEmpresaService.class);
    }

    @Provides
    @Singleton
    public GespecFreteService provideFreteService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecFreteService.class);
    }

    @Provides
    @Singleton
    public GespecRacasService provideRacasService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecRacasService.class);
    }

    @Provides
    @Singleton
    public GespecTipoReferenciaService provideTipoReferenciaService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecTipoReferenciaService.class);
    }

    @Provides
    @Singleton
    public GespecTipoVeiculoFreteService provideTipoVeiculoFreteService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecTipoVeiculoFreteService.class);
    }

    @Provides
    @Singleton
    public GespecValorReferenciaService provideValorReferenciaService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecValorReferenciaService.class);
    }

    @Provides
    @Singleton
    public GespecNegociacaoAnimalService provideNegociacaoAnimalService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecNegociacaoAnimalService.class);
    }

    @Provides
    @Singleton
    public GespecNegociacaoGadoService provideNegociacaoGadoService(@NonNull @Gespec Retrofit retrofit) {
        return retrofit.create(GespecNegociacaoGadoService.class);
    }

    @Provides
    @Singleton
     public GespecAcessoService provideGespecAcessoService(@NonNull @Gespec Retrofit retrofit){
       return retrofit.create(GespecAcessoService.class);
     }

    @Provides
    @Singleton
    public GespecUsuarioService provideGespecUsuarioService(@NonNull @Gespec Retrofit retrofit){
        return retrofit.create(GespecUsuarioService.class);
    }
}
