package com.omni.negociacaobezerros.di.local;

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

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class MapperModule {

    @Provides
    public CategoriaMapper provideCategoriaMapper() {
        return new CategoriaMapperImpl();
    }

    @Provides
    public CorretorMapper provideCorretorMapper() {
        return new CorretorMapperImpl();
    }

    @Provides
    public EmpresaMapper provideEmpresaMapper() {
        return new EmpresaMapperImpl();
    }

    @Provides
    public RotaMapper provideRotaMapper() {
        return new RotaMapperImpl();
    }

    @Provides
    public TransporteMapper provideTransporteMapper() {
        return new TransporteMapperImpl();
    }
}
