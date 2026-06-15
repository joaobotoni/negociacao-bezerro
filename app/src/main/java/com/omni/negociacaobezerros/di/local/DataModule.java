package com.omni.negociacaobezerros.di.local;

import android.content.Context;

import com.omni.negociacaobezerros.data.source.local.AppDatabase;
import com.omni.negociacaobezerros.data.source.local.dao.CapacidadeFreteDao;
import com.omni.negociacaobezerros.data.source.local.dao.CategoriaFreteDao;
import com.omni.negociacaobezerros.data.source.local.dao.CategoriaNegociacaoDao;
import com.omni.negociacaobezerros.data.source.local.dao.CorretorDao;
import com.omni.negociacaobezerros.data.source.local.dao.EmpresaDao;
import com.omni.negociacaobezerros.data.source.local.dao.FreteDao;
import com.omni.negociacaobezerros.data.source.local.dao.NegociacaoAnimalDao;
import com.omni.negociacaobezerros.data.source.local.dao.NegociacaoGadoDao;
import com.omni.negociacaobezerros.data.source.local.dao.RacaDao;
import com.omni.negociacaobezerros.data.source.local.dao.TipoReferenciaDao;
import com.omni.negociacaobezerros.data.source.local.dao.TipoVeiculoFreteDao;
import com.omni.negociacaobezerros.data.source.local.dao.ValorReferenciaDao;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;


@Module
@InstallIn(SingletonComponent.class)
public class DataModule {
    @Provides
    public AppDatabase provideDatabase(@ApplicationContext Context context) {
        return AppDatabase.getDatabase(context);
    }

    @Provides
    public FreteDao provideFreteDao(AppDatabase db) {
        return db.freteDao();
    }

    @Provides
    public CategoriaFreteDao provideCategoriaFreteDao(AppDatabase db) {
        return db.categoriaFreteDao();
    }

    @Provides
    public CapacidadeFreteDao provideCapacidadeFreteDao(AppDatabase db) {
        return db.capacidadeFreteDao();
    }

    @Provides
    public TipoVeiculoFreteDao provideTipoVeiculoFreteDao(AppDatabase db) {
        return db.tipoVeiculoFreteDao();
    }

    @Provides
    public CategoriaNegociacaoDao provideCategoriaNegDao(AppDatabase db) {
        return db.categoriaNegDao();
    }

    @Provides
    public CorretorDao provideCorretorDao(AppDatabase db) {
        return db.corretorDao();
    }

    @Provides
    public EmpresaDao provideEmpresaDao(AppDatabase db) {
        return db.empresaDao();
    }

    @Provides
    public NegociacaoGadoDao provideNegociacaoGadoDao(AppDatabase db) {
        return db.negociacaoGadoDao();
    }

    @Provides
    public NegociacaoAnimalDao provideNegociacaoAnimalDao(AppDatabase db) {
        return db.negociacaoAnimalDao();
    }

    @Provides
    public ValorReferenciaDao provideValorReferenciaDao(AppDatabase db) {
        return db.valorReferenciaDao();
    }

    @Provides
    public TipoReferenciaDao provideTipoReferenciaDao(AppDatabase db) {
        return db.tipoReferenciaDao();
    }

    @Provides
    public RacaDao provideRacaDao(AppDatabase db) {
        return db.racaDao();
    }

}