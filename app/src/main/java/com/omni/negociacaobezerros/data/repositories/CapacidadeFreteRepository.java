package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.CapacidadeFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.CapacidadeFrete;
import com.omni.negociacaobezerros.data.source.remote.gespec.GespecCapacidadeFreteService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class CapacidadeFreteRepository {
    private final CapacidadeFreteDao dao;
    private final RetrofitManager retrofitManager;
    @Inject
    public CapacidadeFreteRepository(CapacidadeFreteDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }
    private GespecCapacidadeFreteService service(){
        return retrofitManager.getRetrofit().create(GespecCapacidadeFreteService.class);
    }

    public List<CapacidadeFrete> getAll() {
        return dao.getAll();
    }

    public Optional<CapacidadeFrete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }
    public List<CapacidadeFrete> findByCategoria(long id) {
        return dao.findByCategoria(id);
    }

    public long insert(CapacidadeFrete capacidadeFrete) {
        return dao.insert(capacidadeFrete);
    }

    public void insertAll(List<CapacidadeFrete> capacidades) {
        dao.insertAll(capacidades);
    }

    public int update(CapacidadeFrete capacidadeFrete) {
        return dao.update(capacidadeFrete);
    }

    public int delete(CapacidadeFrete capacidadeFrete) {
        return dao.delete(capacidadeFrete);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}