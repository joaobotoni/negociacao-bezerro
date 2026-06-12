package com.omni.negociacaobezerros.data.repositories;


import com.omni.negociacaobezerros.data.source.local.dao.EmpresaDao;
import com.omni.negociacaobezerros.data.source.local.entities.Empresa;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecEmpresaService;
import com.omni.negociacaobezerros.di.network.RetrofitManager;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class EmpresaRepository {
    private final EmpresaDao dao;
    private final RetrofitManager retrofitManager;
    @Inject
    public EmpresaRepository(EmpresaDao dao, RetrofitManager retrofitManager) {
        this.dao = dao;
        this.retrofitManager = retrofitManager;
    }

    private GespecEmpresaService service() {
        return retrofitManager.getRetrofit().create(GespecEmpresaService.class);
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