package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.CategoriaNegociacaoDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaNegociacao;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaNegociacaoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class CategoriaNegociacaoRepository {
    private final CategoriaNegociacaoDao dao;
    private final GespecCategoriaNegociacaoService service;
    @Inject
    public CategoriaNegociacaoRepository(CategoriaNegociacaoDao dao, GespecCategoriaNegociacaoService service) {
        this.dao = dao;
        this.service = service;
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