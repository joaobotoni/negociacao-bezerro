package com.omni.negociacaobezerros.di.network.gespec;

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

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class GespecNetworkModule {

    @Provides
    public GespecCapacidadeFreteService provideCapacidadeFreteService(GespecNetworkManager manager) {
        return manager.createService(GespecCapacidadeFreteService.class);
    }

    @Provides
    public GespecCategoriaFreteService provideCategoriaFreteService(GespecNetworkManager manager) {
        return manager.createService(GespecCategoriaFreteService.class);
    }

    @Provides
    public GespecCategoriaNegociacaoService provideCategoriaNegociacaoService(GespecNetworkManager manager) {
        return manager.createService(GespecCategoriaNegociacaoService.class);
    }

    @Provides
    public GespecCorretorService provideCorretorService(GespecNetworkManager manager) {
        return manager.createService(GespecCorretorService.class);
    }

    @Provides
    public GespecEmpresaService provideEmpresaService(GespecNetworkManager manager) {
        return manager.createService(GespecEmpresaService.class);
    }

    @Provides
    public GespecFreteService provideFreteService(GespecNetworkManager manager) {
        return manager.createService(GespecFreteService.class);
    }

    @Provides
    public GespecRacasService provideRacasService(GespecNetworkManager manager) {
        return manager.createService(GespecRacasService.class);
    }

    @Provides
    public GespecTipoReferenciaService provideTipoReferenciaService(GespecNetworkManager manager) {
        return manager.createService(GespecTipoReferenciaService.class);
    }

    @Provides
    public GespecTipoVeiculoFreteService provideTipoVeiculoFreteService(GespecNetworkManager manager) {
        return manager.createService(GespecTipoVeiculoFreteService.class);
    }

    @Provides
    public GespecValorReferenciaService provideValorReferenciaService(GespecNetworkManager manager) {
        return manager.createService(GespecValorReferenciaService.class);
    }

    @Provides
    public GespecNegociacaoAnimalService provideNegociacaoAnimalService(GespecNetworkManager manager) {
        return manager.createService(GespecNegociacaoAnimalService.class);
    }

    @Provides
    public GespecNegociacaoGadoService provideNegociacaoGadoService(GespecNetworkManager manager) {
        return manager.createService(GespecNegociacaoGadoService.class);
    }
}
