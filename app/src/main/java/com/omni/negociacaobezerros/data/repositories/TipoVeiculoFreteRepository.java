package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.TipoVeiculoFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoVeiculoFrete;
import com.omni.negociacaobezerros.data.source.remote.gespec.GespecTipoVeiculoFreteService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class TipoVeiculoFreteRepository {
    private final TipoVeiculoFreteDao dao;
    private final RetrofitManager retrofitManager;

    @Inject
    public TipoVeiculoFreteRepository(TipoVeiculoFreteDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }
    private GespecTipoVeiculoFreteService service(){
        return retrofitManager.getRetrofit().create(GespecTipoVeiculoFreteService.class);
    }

    public List<TipoVeiculoFrete> getAll() {
        return dao.getAll();
    }

    public Optional<TipoVeiculoFrete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(TipoVeiculoFrete tipoVeiculo) {
        return dao.insert(tipoVeiculo);
    }

    public void insertAll(List<TipoVeiculoFrete> tiposVeiculo) {
        dao.insertAll(tiposVeiculo);
    }

    public int update(TipoVeiculoFrete tipoVeiculo) {
        return dao.update(tipoVeiculo);
    }

    public int delete(TipoVeiculoFrete tipoVeiculo) {
        return dao.delete(tipoVeiculo);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}