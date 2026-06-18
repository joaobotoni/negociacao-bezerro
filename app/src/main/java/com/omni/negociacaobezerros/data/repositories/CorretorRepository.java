package com.omni.negociacaobezerros.data.repositories;

import static com.omni.negociacaobezerros.utils.format.Decimals.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.format.Decimals.CEM;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_CALCULO;

import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.CorretorDao;
import com.omni.negociacaobezerros.data.source.local.entities.Corretor;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCorretorService;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class CorretorRepository extends ReadableRepository<Corretor, List<Corretor>> {
    private final CorretorDao dao;
    private final GespecCorretorService service;

    @Inject
    public CorretorRepository(CorretorDao dao, GespecCorretorService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }


    public Optional<Corretor> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public Optional<Byte> buscarTipoDeComissaoPorId(long id) {
        return findById(id).map(Corretor::getTipoComissao)
                .filter(tipo -> !tipo.isEmpty())
                .map(tipo -> tipo.getBytes()[0]);
    }

    public BigDecimal calcularValorPorPercentual(BigDecimal valorTotal, double percentual) {
        return valorTotal.multiply(BigDecimal.valueOf(percentual).divide(CEM, ESCALA_CALCULO, ARREDONDAMENTO_PADRAO));
    }

    public BigDecimal calcularValorPorCabeca(BigDecimal valorPorCabeca, int quatidade) {
        return valorPorCabeca.multiply(new BigDecimal(quatidade));
    }

    public boolean isMesmoTipoComissao(byte c1, byte c2) {
        return c1 == c2;
    }

    @Override
    protected Call<List<Corretor>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<Corretor> data) {
       insertAll(data);
    }
}