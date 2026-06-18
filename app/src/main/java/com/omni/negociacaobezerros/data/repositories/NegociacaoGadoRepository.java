package com.omni.negociacaobezerros.data.repositories;


import com.omni.negociacaobezerros.data.repositories.core.WritableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.NegociacaoGadoDao;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecNegociacaoGadoService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class NegociacaoGadoRepository extends WritableRepository<NegociacaoGado, List<NegociacaoGado>> {
    private final NegociacaoGadoDao dao;
    private final GespecNegociacaoGadoService service;
    @Inject
    public NegociacaoGadoRepository(NegociacaoGadoDao dao, GespecNegociacaoGadoService service) {
        super(dao);
        this.dao = dao;
        this.service = service;
    }

    public Optional<NegociacaoGado> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }


    @Override
    protected Call<List<NegociacaoGado>> call(List<NegociacaoGado> data) {
        return service.insertAll(data);
    }

    @Override
    protected List<NegociacaoGado> load() {
        return getAll();
    }
}