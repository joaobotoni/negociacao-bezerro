package com.omni.negociacaobezerros.data.repositories;


import com.omni.negociacaobezerros.data.source.local.dao.NegociacaoGadoDao;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecNegociacaoGadoService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class NegociacaoGadoRepository {
    private final NegociacaoGadoDao dao;
    private final GespecNegociacaoGadoService service;
    @Inject
    public NegociacaoGadoRepository(NegociacaoGadoDao dao, GespecNegociacaoGadoService service) {
        this.dao = dao;
        this.service = service;
    }

    public List<NegociacaoGado> getAll() {
        return dao.getAll();
    }

    public Optional<NegociacaoGado> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(NegociacaoGado negociacaoGado) {
        return dao.insert(negociacaoGado);
    }

    public void insertAll(List<NegociacaoGado> negociacaoGados) {
        dao.insertAll(negociacaoGados);
    }

    public int update(NegociacaoGado negociacaoGado) {
        return dao.update(negociacaoGado);
    }

    public int delete(NegociacaoGado negociacaoGado) {
        return dao.delete(negociacaoGado);
    }

    public void deleteAll() {
        dao.deleteAll();
    }
}