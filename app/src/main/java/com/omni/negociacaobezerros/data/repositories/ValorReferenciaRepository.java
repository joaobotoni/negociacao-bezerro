package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.ValorReferenciaDao;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecValorReferenciaService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;


public class ValorReferenciaRepository {
    private final ValorReferenciaDao dao;
    private final RetrofitManager retrofitManager;
    @Inject
    public ValorReferenciaRepository(ValorReferenciaDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }

    private GespecValorReferenciaService service() {
        return retrofitManager.getRetrofit().create(GespecValorReferenciaService.class);
    }

    public List<ValorReferencia> getAll() {
        return dao.getAll();
    }

    public Optional<ValorReferencia> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public Optional<ValorReferencia> findMaisRecente() {
        return Optional.ofNullable(dao.findMaisRecente());
    }

    public long insert(ValorReferencia valorReferencia) {
        return dao.insert(valorReferencia);
    }

    public void insertAll(List<ValorReferencia> valorReferencias) {
        dao.insertAll(valorReferencias);
    }

    public int update(ValorReferencia valorReferencia) {
        return dao.update(valorReferencia);
    }

    public int delete(ValorReferencia valorReferencia) {
        return dao.delete(valorReferencia);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}