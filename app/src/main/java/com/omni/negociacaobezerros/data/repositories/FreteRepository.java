package com.omni.negociacaobezerros.data.repositories;

import static com.omni.negociacaobezerros.utils.format.Decimals.ARREDONDAMENTO_FINANCEIRO;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_CALCULO;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_MONETARIA;

import com.omni.negociacaobezerros.data.models.PrecificacaoFrete;
import com.omni.negociacaobezerros.data.models.Transporte;
import com.omni.negociacaobezerros.data.source.local.dao.FreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.Frete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecFreteService;

import java.math.BigDecimal;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import retrofit2.Response;

@Singleton
public class FreteRepository {
    private final FreteDao dao;
    private final Provider<GespecFreteService> serviceProvider;
    @Inject
    public FreteRepository(FreteDao dao, Provider<GespecFreteService> serviceProvider) {
        this.dao = dao;
        this.serviceProvider = serviceProvider;
    }

    public List<Frete> sincronizar(String usuario) throws IOException {
        Response<List<Frete>> response = serviceProvider.get().getAll(usuario).execute();
        if (!response.isSuccessful() || response.body() == null) {
            throw new IOException("Falha ao sincronizar fretes: HTTP " + response.code());
        }
        List<Frete> remotos = response.body();
        dao.insertAll(remotos);
        return remotos;
    }


    public List<Frete> getAll() {
        return dao.getAll();
    }

    public Optional<Frete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }
    public Optional<Frete> buscarPorVeiculoEDistancia(long idVeiculo, double distancia) {
        return Optional.ofNullable(dao.findByVehicleAndDistance(idVeiculo, distancia));
    }

    public long insert(Frete frete) {
        return dao.insert(frete);
    }

    public void insertAll(List<Frete> fretes) {
        dao.insertAll(fretes);
    }

    public int update(Frete frete) {
        return dao.update(frete);
    }

    public int delete(Frete frete) {
        return dao.delete(frete);
    }

    public void deleteAll() {
        dao.deleteAll();
    }

    public PrecificacaoFrete calcularFrete(List<Transporte> transportes, double distancia, int cargaTotal, BigDecimal pesoMedio) {
        BigDecimal totalFrete = calcularFreteTotal(transportes, distancia);
        BigDecimal valorPorKg = calcularFretePorKg(totalFrete, pesoMedio, cargaTotal);
        return new PrecificacaoFrete(totalFrete, valorPorKg);
    }
    public BigDecimal calcularFreteTotal(List<Transporte> transportes, double distancia) {
        BigDecimal total = BigDecimal.ZERO;
        for (Transporte transporte : transportes) {
            Frete frete = buscarPorVeiculoEDistancia(transporte.getId(), distancia)
                    .orElseThrow(() -> new IllegalStateException("Nenhum frete configurado para o veículo: " + transporte.getNomeVeiculo()));
            BigDecimal custoUnitario = calcularCustoUnitario(frete, distancia);
            BigDecimal quantidade = BigDecimal.valueOf(transporte.getQuantidade());
            BigDecimal subtotal = custoUnitario.multiply(quantidade);
            total = total.add(subtotal);
        }

        return total.setScale(ESCALA_MONETARIA, ARREDONDAMENTO_FINANCEIRO);
    }

    public BigDecimal calcularFretePorKg(BigDecimal valorTotalFrete, BigDecimal pesoMedio, int cargaTotal) {
        BigDecimal pesoTotal = pesoMedio.multiply(BigDecimal.valueOf(cargaTotal));
        return valorTotalFrete.divide(pesoTotal, ESCALA_CALCULO, ARREDONDAMENTO_FINANCEIRO)
                .setScale(ESCALA_MONETARIA, ARREDONDAMENTO_FINANCEIRO);
    }

    public BigDecimal calcularCustoUnitario(Frete frete, double distancia) {
        BigDecimal valorBase = BigDecimal.valueOf(frete.getValor());
        if (frete.getTipoCobranca() == 1) {
            BigDecimal multiplicadorDistancia = BigDecimal.valueOf(distancia);
            return valorBase.multiply(multiplicadorDistancia);
        }
        return valorBase;
    }
}