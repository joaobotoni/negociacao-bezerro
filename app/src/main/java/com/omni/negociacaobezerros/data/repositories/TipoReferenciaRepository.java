package com.omni.negociacaobezerros.data.repositories;



import com.omni.negociacaobezerros.data.source.local.dao.TipoReferenciaDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoReferencia;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoReferenciaService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class TipoReferenciaRepository {
    private final TipoReferenciaDao dao;

    private final RetrofitManager retrofitManager;

    @Inject
    public TipoReferenciaRepository(TipoReferenciaDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }

    private GespecTipoReferenciaService service(){
        return retrofitManager.getRetrofit().create(GespecTipoReferenciaService.class);
    }

    public List<TipoReferencia> getAll() {
        return dao.getAll();
    }

    public Optional<TipoReferencia> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(TipoReferencia tipoReferencia) {
        return dao.insert(tipoReferencia);
    }

    public void insertAll(List<TipoReferencia> tipoReferencias) {
        dao.insertAll(tipoReferencias);
    }

    public int update(TipoReferencia tipoReferencia) {
        return dao.update(tipoReferencia);
    }

    public int delete(TipoReferencia tipoReferencia) {
        return dao.delete(tipoReferencia);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}