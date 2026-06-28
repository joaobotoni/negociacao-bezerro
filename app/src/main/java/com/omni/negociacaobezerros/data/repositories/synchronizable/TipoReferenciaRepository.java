package com.omni.negociacaobezerros.data.repositories.synchronizable;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.TipoReferenciaDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoReferencia;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoReferenciaService;
import com.omni.negociacaobezerros.helpers.TaskHelper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class TipoReferenciaRepository extends ReadableRepository<TipoReferencia, List<TipoReferencia>> {
    private final TipoReferenciaDao dao;
    private final GespecTipoReferenciaService service;
    @Inject
    public TipoReferenciaRepository(TipoReferenciaDao dao, GespecTipoReferenciaService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }

    public Optional<TipoReferencia> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    @Override
    protected Call<List<TipoReferencia>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<TipoReferencia> data) {
       insertAll(data);
    }
}