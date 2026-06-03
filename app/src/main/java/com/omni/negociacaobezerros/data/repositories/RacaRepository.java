package com.omni.negociacaobezerros.data.repositories;



import com.omni.negociacaobezerros.data.source.local.dao.RacaDao;
import com.omni.negociacaobezerros.data.source.local.entities.Raca;
import com.omni.negociacaobezerros.data.source.remote.gespec.GespecRacasService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class RacaRepository {
    private final RacaDao dao;
    private final RetrofitManager retrofitManager;

    @Inject
    public RacaRepository(RacaDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }

    private GespecRacasService service() {
        return retrofitManager.getRetrofit().create(GespecRacasService.class);
    }

    public List<Raca> getAll() {
        return dao.getAll();
    }

    public Optional<Raca> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(Raca raca) {
        return dao.insert(raca);
    }

    public void insertAll(List<Raca> racas) {dao.insertAll(racas);}

    public int update(Raca raca) {return dao.update(raca);}

    public int delete(Raca raca) {
        return dao.delete(raca);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}

