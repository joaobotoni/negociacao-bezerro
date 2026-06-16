package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.TipoVeiculoFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoVeiculoFrete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoVeiculoFreteService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class TipoVeiculoFreteRepository {
    private final TipoVeiculoFreteDao dao;
    private final GespecTipoVeiculoFreteService service;
    @Inject
    public TipoVeiculoFreteRepository(TipoVeiculoFreteDao dao, GespecTipoVeiculoFreteService service) {
        this.dao = dao;
        this.service = service;
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