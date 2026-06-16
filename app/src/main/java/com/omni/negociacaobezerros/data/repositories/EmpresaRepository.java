package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.EmpresaDao;
import com.omni.negociacaobezerros.data.source.local.entities.Empresa;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecEmpresaService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class EmpresaRepository {
    private final EmpresaDao dao;
    private final GespecEmpresaService service;
    @Inject
    public EmpresaRepository(EmpresaDao dao, GespecEmpresaService service) {
        this.dao = dao;
        this.service = service;
    }


    public List<Empresa> getAll() {
        return dao.getAll();
    }

    public Optional<Empresa> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(Empresa empresa) {
        return dao.insert(empresa);
    }

    public void insertAll(List<Empresa> empresas) {
        dao.insertAll(empresas);
    }

    public int update(Empresa empresa) {
        return dao.update(empresa);
    }

    public int delete(Empresa empresa) {
        return dao.delete(empresa);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}