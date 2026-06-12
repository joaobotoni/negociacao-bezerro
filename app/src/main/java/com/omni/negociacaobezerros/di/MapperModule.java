package com.omni.negociacaobezerros.di;

import com.omni.negociacaobezerros.utils.mapper.CategoriaMapper;
import com.omni.negociacaobezerros.utils.mapper.CorretorMapper;
import com.omni.negociacaobezerros.utils.mapper.EmpresaMapper;
import com.omni.negociacaobezerros.utils.mapper.RotaMapper;
import com.omni.negociacaobezerros.utils.mapper.TransporteMapper;
import com.omni.negociacaobezerros.utils.mapper.CategoriaMapperImpl;
import com.omni.negociacaobezerros.utils.mapper.CorretorMapperImpl;
import com.omni.negociacaobezerros.utils.mapper.EmpresaMapperImpl;
import com.omni.negociacaobezerros.utils.mapper.RotaMapperImpl;
import com.omni.negociacaobezerros.utils.mapper.TransporteMapperImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class MapperModule {

    @Provides
    @Singleton
    public CategoriaMapper provideCategoriaMapper() {
        return new CategoriaMapperImpl();
    }

    @Provides
    @Singleton
    public CorretorMapper provideCorretorMapper() {
        return new CorretorMapperImpl();
    }

    @Provides
    @Singleton
    public EmpresaMapper provideEmpresaMapper() {
        return new EmpresaMapperImpl();
    }

    @Provides
    @Singleton
    public RotaMapper provideRotaMapper() {
        return new RotaMapperImpl();
    }

    @Provides
    @Singleton
    public TransporteMapper provideTransporteMapper() {
        return new TransporteMapperImpl();
    }
}
