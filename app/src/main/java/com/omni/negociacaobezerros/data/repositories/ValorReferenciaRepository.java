package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.ValorReferenciaDao;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecValorReferenciaService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class ValorReferenciaRepository {
    private final ValorReferenciaDao dao;
    private final Provider<GespecValorReferenciaService> serviceProvider;
    @Inject
    public ValorReferenciaRepository(ValorReferenciaDao dao, Provider<GespecValorReferenciaService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<ValorReferencia> sincronizar(String usuario) throws IOException {
        Response<List<ValorReferencia>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar valores de referência: HTTP " + response.code());
        }
        List<ValorReferencia> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
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