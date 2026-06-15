package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.CategoriaNegociacaoDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaNegociacao;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaNegociacaoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class CategoriaNegociacaoRepository {
    private final CategoriaNegociacaoDao dao;
    private final Provider<GespecCategoriaNegociacaoService> serviceProvider;
    @Inject
    public CategoriaNegociacaoRepository(CategoriaNegociacaoDao dao, Provider<GespecCategoriaNegociacaoService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<CategoriaNegociacao> sincronizar(String usuario) throws IOException {
        Response<List<CategoriaNegociacao>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar categorias de negociação: HTTP " + response.code());
        }
        List<CategoriaNegociacao> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
    }

    public List<CategoriaNegociacao> getAll() {
        return dao.getAll();
    }

    public Optional<CategoriaNegociacao> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(CategoriaNegociacao categoriaNegociacao) {
        return dao.insert(categoriaNegociacao);
    }

    public void insertAll(List<CategoriaNegociacao> categoriaNegociacaos) {
        dao.insertAll(categoriaNegociacaos);
    }

    public int update(CategoriaNegociacao categoriaNegociacao) {
        return dao.update(categoriaNegociacao);
    }

    public int delete(CategoriaNegociacao categoriaNegociacao) {
        return dao.delete(categoriaNegociacao);
    }
    public void deleteAll() {
        dao.deleteAll();
    }
}