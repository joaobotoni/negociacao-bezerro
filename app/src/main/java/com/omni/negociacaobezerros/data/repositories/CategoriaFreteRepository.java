package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.CategoriaFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCategoriaFreteService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class CategoriaFreteRepository {
    private final CategoriaFreteDao dao;
    private final GespecCategoriaFreteService service;
    @Inject
    public CategoriaFreteRepository(CategoriaFreteDao dao, GespecCategoriaFreteService service) {
        this.dao = dao;
        this.service = service;
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