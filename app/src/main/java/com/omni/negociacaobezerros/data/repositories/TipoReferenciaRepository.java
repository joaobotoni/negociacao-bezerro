package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.TipoReferenciaDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoReferencia;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoReferenciaService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class TipoReferenciaRepository {
    private final TipoReferenciaDao dao;
    private final Provider<GespecTipoReferenciaService> serviceProvider;
    @Inject
    public TipoReferenciaRepository(TipoReferenciaDao dao, Provider<GespecTipoReferenciaService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<TipoReferencia> sincronizar(String usuario) throws IOException {
        Response<List<TipoReferencia>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar tipos de referência: HTTP " + response.code());
        }
        List<TipoReferencia> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
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