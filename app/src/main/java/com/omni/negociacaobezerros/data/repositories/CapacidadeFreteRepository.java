package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.CapacidadeFreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.CapacidadeFrete;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCapacidadeFreteService;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class CapacidadeFreteRepository extends ReadableRepository<CapacidadeFrete, List<CapacidadeFrete>> {
    private final CapacidadeFreteDao dao;
    private final GespecCapacidadeFreteService service;

    @Inject
    public CapacidadeFreteRepository(CapacidadeFreteDao dao, GespecCapacidadeFreteService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;

    }

    public Optional<CapacidadeFrete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public List<CapacidadeFrete> findByCategoria(long id) {
        return dao.findByCategoria(id);
    }

    @Override
    protected Call<List<CapacidadeFrete>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<CapacidadeFrete> data) {
        insertAll(data);
    }
}