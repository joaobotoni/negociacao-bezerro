package com.omni.negociacaobezerros.data.repositories;



import com.omni.negociacaobezerros.data.source.local.dao.CategoriaFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;
import com.omni.negociacaobezerros.data.source.remote.gespec.GespecCategoriaFreteService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class CategoriaFreteRepository {
    private final CategoriaFreteDao dao;

    private final RetrofitManager retrofitManager;
    @Inject
    public CategoriaFreteRepository(CategoriaFreteDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }

    private GespecCategoriaFreteService service(){
        return retrofitManager.getRetrofit().create(GespecCategoriaFreteService.class);
    }

    public List<CategoriaFrete> getAll() {
        return dao.getAll();
    }

    public Optional<CategoriaFrete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(CategoriaFrete categoriaFrete) {
        return dao.insert(categoriaFrete);
    }

    public void insertAll(List<CategoriaFrete> categorias) {
        dao.insertAll(categorias);
    }

    public int update(CategoriaFrete categoriaFrete) {
        return dao.update(categoriaFrete);
    }

    public int delete(CategoriaFrete categoriaFrete) {
        return dao.delete(categoriaFrete);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}