package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.TipoVeiculoFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoVeiculoFrete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecTipoVeiculoFreteService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class TipoVeiculoFreteRepository {
    private final TipoVeiculoFreteDao dao;
    private final Provider<GespecTipoVeiculoFreteService> serviceProvider;
    @Inject
    public TipoVeiculoFreteRepository(TipoVeiculoFreteDao dao, Provider<GespecTipoVeiculoFreteService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<TipoVeiculoFrete> sincronizar(String usuario) throws IOException {
        Response<List<TipoVeiculoFrete>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar tipos de veículo de frete: HTTP " + response.code());
        }
        List<TipoVeiculoFrete> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
    }


    public List<TipoVeiculoFrete> getAll() {
        return dao.getAll();
    }

    public Optional<TipoVeiculoFrete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(TipoVeiculoFrete tipoVeiculo) {
        return dao.insert(tipoVeiculo);
    }

    public void insertAll(List<TipoVeiculoFrete> tiposVeiculo) {
        dao.insertAll(tiposVeiculo);
    }

    public int update(TipoVeiculoFrete tipoVeiculo) {
        return dao.update(tipoVeiculo);
    }

    public int delete(TipoVeiculoFrete tipoVeiculo) {
        return dao.delete(tipoVeiculo);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}