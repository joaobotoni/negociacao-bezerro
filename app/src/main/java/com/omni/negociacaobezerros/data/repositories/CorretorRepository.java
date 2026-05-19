package com.omni.negociacaobezerros.data.repositories;



import static com.omni.negociacaobezerros.utils.DecimalUtil.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.DecimalUtil.CEM;
import static com.omni.negociacaobezerros.utils.DecimalUtil.ESCALA_CALCULO;

import com.omni.negociacaobezerros.data.source.local.dao.CorretorDao;
import com.omni.negociacaobezerros.data.source.local.entities.Corretor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

public class CorretorRepository {
    private final CorretorDao dao;
    @Inject
    public CorretorRepository(CorretorDao dao) {
        this.dao = dao;
    }

    public List<Corretor> getAll() {
        return dao.getAll();
    }

    public Optional<Corretor> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public long insert(Corretor corretor) {
        return dao.insert(corretor);
    }

    public void insertAll(List<Corretor> corretores) {
        dao.insertAll(corretores);
    }

    public int update(Corretor corretor) {
        return dao.update(corretor);
    }

    public int delete(Corretor corretor) {
        return dao.delete(corretor);
    }

    public void deleteAll() {
        dao.deleteAll();
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
}