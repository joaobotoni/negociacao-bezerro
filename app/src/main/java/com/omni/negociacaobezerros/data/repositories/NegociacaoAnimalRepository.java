package com.omni.negociacaobezerros.data.repositories;


import com.omni.negociacaobezerros.data.source.local.dao.NegociacaoAnimalDao;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoAnimal;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecNegociacaoAnimalService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class NegociacaoAnimalRepository {
    private final NegociacaoAnimalDao dao;
    private final Provider<GespecNegociacaoAnimalService> serviceProvider;
    @Inject
    public NegociacaoAnimalRepository(NegociacaoAnimalDao dao, Provider<GespecNegociacaoAnimalService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<NegociacaoAnimal> sincronizar(String usuario) throws IOException {
        List<NegociacaoAnimal> pendentes = dao.getAll();
        Response<List<NegociacaoAnimal>> response = serviceProvider.get().insertAll(usuario, pendentes).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao enviar negociações de animais: HTTP " + response.code());
        }
        return response.body();
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