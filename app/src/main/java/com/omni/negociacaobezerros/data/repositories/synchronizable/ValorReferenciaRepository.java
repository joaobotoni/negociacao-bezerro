package com.omni.negociacaobezerros.data.repositories.synchronizable;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.ValorReferenciaDao;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecValorReferenciaService;
import com.omni.negociacaobezerros.helpers.TaskHelper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class ValorReferenciaRepository extends ReadableRepository<ValorReferencia, List<ValorReferencia>> {
    private final ValorReferenciaDao dao;
    private final GespecValorReferenciaService service;
    @Inject
    public ValorReferenciaRepository(ValorReferenciaDao dao, GespecValorReferenciaService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }


    public Optional<ValorReferencia> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public Optional<ValorReferencia> findMaisRecente() {
        return Optional.ofNullable(dao.findMaisRecente());
    }

    @Override
    protected Call<List<ValorReferencia>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<ValorReferencia> data) {
         insertAll(data);
    }
}