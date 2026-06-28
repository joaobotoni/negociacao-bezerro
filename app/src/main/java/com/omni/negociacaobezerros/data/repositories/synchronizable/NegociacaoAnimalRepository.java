package com.omni.negociacaobezerros.data.repositories.synchronizable;


import com.omni.negociacaobezerros.data.repositories.core.WritableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.NegociacaoAnimalDao;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoAnimal;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecNegociacaoAnimalService;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class NegociacaoAnimalRepository extends WritableRepository<NegociacaoAnimal, List<NegociacaoAnimal>> {
    private final NegociacaoAnimalDao dao;
    private final GespecNegociacaoAnimalService service;

    @Inject
    public NegociacaoAnimalRepository(NegociacaoAnimalDao dao, GespecNegociacaoAnimalService service) {
        super(dao);
        this.dao = dao;
        this.service = service;
    }

    public Optional<NegociacaoAnimal> findById(long id1, long id2) {
        return Optional.ofNullable(dao.findById(id1, id2));
    }


    @Override
    protected Call<List<NegociacaoAnimal>> call(List<NegociacaoAnimal> data) {
        return service.insertAll(data);
    }

    @Override
    protected List<NegociacaoAnimal> load() {
        return getAll();
    }
}