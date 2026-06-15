package com.omni.negociacaobezerros.data.repositories;

import static com.omni.negociacaobezerros.utils.format.Decimals.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.format.Decimals.CEM;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_CALCULO;

import com.omni.negociacaobezerros.data.source.local.dao.CorretorDao;
import com.omni.negociacaobezerros.data.source.local.entities.Corretor;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecCorretorService;

import java.math.BigDecimal;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class CorretorRepository {
    private final CorretorDao dao;
    private final Provider<GespecCorretorService> serviceProvider;

    @Inject
    public CorretorRepository(CorretorDao dao, Provider<GespecCorretorService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<Corretor> sincronizar(String usuario) throws IOException {
        Response<List<Corretor>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar corretores: HTTP " + response.code());
        }
        List<Corretor> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
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