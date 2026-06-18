package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.TipoVeiculoFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoVeiculoFrete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoVeiculoFreteService;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class TipoVeiculoFreteRepository extends ReadableRepository<TipoVeiculoFrete, List<TipoVeiculoFrete>> {
    private final TipoVeiculoFreteDao dao;
    private final GespecTipoVeiculoFreteService service;

    @Inject
    public TipoVeiculoFreteRepository(TipoVeiculoFreteDao dao, GespecTipoVeiculoFreteService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }

    public Optional<TipoVeiculoFrete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    @Override
    protected Call<List<TipoVeiculoFrete>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<TipoVeiculoFrete> data) {
        insertAll(data);
    }
}