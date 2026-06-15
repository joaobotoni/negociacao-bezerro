package com.omni.negociacaobezerros.data.repositories;



import com.omni.negociacaobezerros.data.source.local.dao.RacaDao;
import com.omni.negociacaobezerros.data.source.local.entities.Raca;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecRacasService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class RacaRepository {
    private final RacaDao dao;
    private final Provider<GespecRacasService> serviceProvider;
    @Inject
    public RacaRepository(RacaDao dao, Provider<GespecRacasService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<Raca> sincronizar(String usuario) throws IOException {
        Response<List<Raca>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar raças: HTTP " + response.code());
        }
        List<Raca> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
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

    public int update(Raca raca) {
        return dao.update(raca);
    }

    public int delete(Raca raca) {
        return dao.delete(raca);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}

