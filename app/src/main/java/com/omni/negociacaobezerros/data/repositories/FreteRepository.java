package com.omni.negociacaobezerros.data.repositories;

import static com.omni.negociacaobezerros.utils.format.Decimals.ARREDONDAMENTO_FINANCEIRO;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_CALCULO;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_MONETARIA;

import com.omni.negociacaobezerros.data.models.PrecificacaoFrete;
import com.omni.negociacaobezerros.data.models.Transporte;
import com.omni.negociacaobezerros.data.repositories.core.ReadableRepository;
import com.omni.negociacaobezerros.data.source.local.dao.FreteDao;
import com.omni.negociacaobezerros.data.source.local.entities.Frete;
import com.omni.negociacaobezerros.data.source.network.gespec.GespecFreteService;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;

import java.math.BigDecimal;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Response;

@Singleton
public class FreteRepository extends ReadableRepository<Frete, List<Frete>> {
    private final FreteDao dao;
    private final GespecFreteService service;

    @Inject
    public FreteRepository(FreteDao dao, GespecFreteService service, TaskHelper taskHelper) {
        super(dao, taskHelper);
        this.dao = dao;
        this.service = service;
    }


    public Optional<Frete> findById(long id) {
        return Optional.ofNullable(dao.findById(id));
    }

    public Optional<Frete> buscarPorVeiculoEDistancia(long idVeiculo, double distancia) {
        return Optional.ofNullable(dao.findByVehicleAndDistance(idVeiculo, distancia));
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

    @Override
    protected Call<List<Frete>> call() {
        return service.getAll();
    }

    @Override
    protected void save(List<Frete> data) {
        insertAll(data);
    }
}