package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.CapacidadeFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.CapacidadeFrete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCapacidadeFreteService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class CapacidadeFreteRepository {
    private final CapacidadeFreteDao dao;
    private final Provider<GespecCapacidadeFreteService> serviceProvider;
    @Inject
    public CapacidadeFreteRepository(CapacidadeFreteDao dao, Provider<GespecCapacidadeFreteService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<CapacidadeFrete> sincronizar(String usuario) throws IOException {
        Response<List<CapacidadeFrete>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar capacidades de frete: HTTP " + response.code());
        }
        List<CapacidadeFrete> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
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