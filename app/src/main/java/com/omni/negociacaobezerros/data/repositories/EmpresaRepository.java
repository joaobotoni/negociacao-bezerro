package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.EmpresaDao;
import com.omni.negociacaobezerros.data.source.local.entities.Empresa;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecEmpresaService;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class EmpresaRepository extends ReadableRepository<Empresa, List<Empresa>> {
    private final EmpresaDao dao;
    private final GespecEmpresaService service;

    @Inject
    public EmpresaRepository(EmpresaDao dao, GespecEmpresaService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }

    public Optional<Empresa> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    @Override
    protected Call<List<Empresa>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<Empresa> data) {
        insertAll(data);
    }
}