package com.omni.negociacaobezerros.data.repositories.synchronizable;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.CategoriaFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaFreteService;
import com.omni.negociacaobezerros.helpers.TaskHelper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class CategoriaFreteRepository extends ReadableRepository<CategoriaFrete, List<CategoriaFrete>> {
    private final CategoriaFreteDao dao;
    private final GespecCategoriaFreteService service;

    @Inject
    public CategoriaFreteRepository(CategoriaFreteDao dao, GespecCategoriaFreteService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }


    public Optional<CategoriaFrete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    @Override
    protected Call<List<CategoriaFrete>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<CategoriaFrete> data) {
        insertAll(data);
    }
}