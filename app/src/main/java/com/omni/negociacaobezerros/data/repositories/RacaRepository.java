package com.omni.negociacaobezerros.data.repositories;


import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.RacaDao;
import com.omni.negociacaobezerros.data.source.local.entities.Raca;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecRacasService;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class RacaRepository extends ReadableRepository<Raca, List<Raca>> {
    private final RacaDao dao;
    private final GespecRacasService service;

    @Inject
    public RacaRepository(RacaDao dao, GespecRacasService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }

    public Optional<Raca> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    @Override
    protected Call<List<Raca>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<Raca> data) {
        insertAll(data);
    }
}

