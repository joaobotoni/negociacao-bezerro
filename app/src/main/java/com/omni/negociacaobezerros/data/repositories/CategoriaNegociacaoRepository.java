package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.CategoriaNegociacaoDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaNegociacao;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaNegociacaoService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class CategoriaNegociacaoRepository {

    private final CategoriaNegociacaoDao dao;
    private final RetrofitManager retrofitManager;

    @Inject
    public CategoriaNegociacaoRepository(CategoriaNegociacaoDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }

    private GespecCategoriaNegociacaoService service() {
        return retrofitManager.getRetrofit().create(GespecCategoriaNegociacaoService.class);
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

    public void delete() {
        dao.deleteAll();
    }
}