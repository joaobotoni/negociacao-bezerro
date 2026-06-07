package com.omni.negociacaobezerros.data.repositories;


import com.omni.negociacaobezerros.data.source.local.dao.NegociacaoAnimalDao;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoAnimal;
import com.omni.negociacaobezerros.data.source.remote.gespec.GespecNegociacaoAnimalService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class NegociacaoAnimalRepository {
    private final NegociacaoAnimalDao dao;
    private final RetrofitManager retrofitManager;

    @Inject
    public NegociacaoAnimalRepository(NegociacaoAnimalDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }

    private GespecNegociacaoAnimalService service(){
        return retrofitManager.getRetrofit().create(GespecNegociacaoAnimalService.class);
    }

    public List<NegociacaoAnimal> getAll() {
        return dao.getAll();
    }

    public Optional<NegociacaoAnimal> findById(long id1, long id2) {
        return Optional.ofNullable(dao.findById(id1, id2));
    }

    public long insert(NegociacaoAnimal negociacaoAnimal) {
        return dao.insert(negociacaoAnimal);
    }

    public void insertAll(List<NegociacaoAnimal> negociacaoAnimals) {
        dao.insertAll(negociacaoAnimals);
    }

    public int update(NegociacaoAnimal negociacaoAnimal) {
        return dao.update(negociacaoAnimal);
    }

    public int delete(NegociacaoAnimal negociacaoAnimal) {
        return dao.delete(negociacaoAnimal);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}