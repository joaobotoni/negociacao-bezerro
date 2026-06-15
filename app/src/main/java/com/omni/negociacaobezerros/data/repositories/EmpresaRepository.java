package com.omni.negociacaobezerros.data.repositories;

import com.omni.negociacaobezerros.data.source.local.dao.EmpresaDao;
import com.omni.negociacaobezerros.data.source.local.entities.Empresa;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecEmpresaService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class EmpresaRepository {
    private final EmpresaDao dao;
    private final Provider<GespecEmpresaService> serviceProvider;
    @Inject
    public EmpresaRepository(EmpresaDao dao, Provider<GespecEmpresaService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<Empresa> sincronizar(String usuario) throws IOException {
        Response<List<Empresa>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar empresas: HTTP " + response.code());
        }
        List<Empresa> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
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