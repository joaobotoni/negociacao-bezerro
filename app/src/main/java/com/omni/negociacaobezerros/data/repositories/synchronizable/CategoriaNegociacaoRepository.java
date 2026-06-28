package com.omni.negociacaobezerros.data.repositories.synchronizable;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.CategoriaNegociacaoDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaNegociacao;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaNegociacaoService;
import com.omni.negociacaobezerros.helpers.TaskHelper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class CategoriaNegociacaoRepository extends ReadableRepository<CategoriaNegociacao, List<CategoriaNegociacao>> {
    private final CategoriaNegociacaoDao dao;
    private final GespecCategoriaNegociacaoService service;
    @Inject
    public CategoriaNegociacaoRepository(CategoriaNegociacaoDao dao, GespecCategoriaNegociacaoService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }

    public Optional<CategoriaNegociacao> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    @Override
    protected Call<List<CategoriaNegociacao>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<CategoriaNegociacao> data) {
        insertAll(data);
    }
}