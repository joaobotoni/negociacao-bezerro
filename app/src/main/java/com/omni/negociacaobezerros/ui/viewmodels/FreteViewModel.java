package com.omni.negociacaobezerros.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.PrecificacaoFrete;
import com.omni.negociacaobezerros.data.models.Transporte;
import com.omni.negociacaobezerros.data.repositories.TransporteRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.FreteRepository;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.FreteUiState;
import com.omni.negociacaobezerros.ui.states.TransporteUiState;
import com.omni.negociacaobezerros.utils.mapper.TransporteMapper;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FreteViewModel extends ViewModel {
    private final FreteRepository freteRepository;
    private final TransporteRepository transporteRepository;
    private final TransporteMapper transporteMapper;
    private final TaskHelper taskHelper;

    private final MutableLiveData<FreteUiState> freteState = new MutableLiveData<>(null);
    private final MutableLiveData<List<TransporteUiState>> transportesState = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> freteCalculado = new MutableLiveData<>(false);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    private final MutableLiveData<Long> idCategoria = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> quantidade = new MutableLiveData<>(null);
    private final MutableLiveData<BigDecimal> pesoMedio = new MutableLiveData<>(null);
    private final MutableLiveData<Double> distanciaKm = new MutableLiveData<>(null);

    @Inject
    public FreteViewModel(FreteRepository freteRepository, TransporteRepository transporteRepository,
                           TransporteMapper transporteMapper, TaskHelper taskHelper) {
        this.freteRepository = freteRepository;
        this.transporteRepository = transporteRepository;
        this.transporteMapper = transporteMapper;
        this.taskHelper = taskHelper;
    }

    public LiveData<FreteUiState> getFreteState() { return freteState; }
    public LiveData<List<TransporteUiState>> getTransportesState() { return transportesState; }
    public LiveData<Boolean> getFreteCalculado() { return freteCalculado; }
    public LiveData<Throwable> getErro() { return erro; }

    public void setCategoria(Long idCategoriaSelecionada) {
        idCategoria.setValue(idCategoriaSelecionada);
        tentarCalcular();
    }

    public void setQuantidade(Integer quantidadeInformada) {
        quantidade.setValue(quantidadeInformada);
        tentarCalcular();
    }

    public void setPesoMedio(BigDecimal pesoMedioInformado) {
        pesoMedio.setValue(pesoMedioInformado);
        tentarCalcular();
    }

    public void setDistanciaKm(Double distanciaInformada) {
        distanciaKm.setValue(distanciaInformada);
        tentarCalcular();
    }

    public void limpar() {
        freteState.setValue(null);
        transportesState.setValue(null);
        freteCalculado.setValue(false);
        erro.setValue(null);
        idCategoria.setValue(null);
        quantidade.setValue(null);
        pesoMedio.setValue(null);
        distanciaKm.setValue(null);
    }

    private void tentarCalcular() {
        if (!isProntoParaCalcular()) return;
        calcular(idCategoria.getValue(), quantidade.getValue(), distanciaKm.getValue(), pesoMedio.getValue());
    }

    private boolean isProntoParaCalcular() {
        return idCategoria.getValue() != null && quantidade.getValue() != null
                && pesoMedio.getValue() != null && isDistanciaValida(distanciaKm.getValue());
    }

    private boolean isDistanciaValida(Double distancia) {
        return distancia != null && distancia > 0;
    }

    private void calcular(long idCategoriaAtual, int quantidadeAtual, double distanciaAtual, BigDecimal pesoMedioAtual) {
        taskHelper.execute(
                () -> calcularFreteETransportes(idCategoriaAtual, quantidadeAtual, distanciaAtual, pesoMedioAtual),
                this::postResultado, erro::setValue);
    }

    private ResultadoCalculoFrete calcularFreteETransportes(long idCategoriaAtual, int quantidadeAtual, double distanciaAtual, BigDecimal pesoMedioAtual) {
        List<Transporte> transportes = transporteRepository.recomendarTransportes(idCategoriaAtual, quantidadeAtual);
        PrecificacaoFrete precificacao = freteRepository.calcularFrete(transportes, distanciaAtual, quantidadeAtual, pesoMedioAtual);
        return new ResultadoCalculoFrete(transportes, precificacao);
    }

    private void postResultado(ResultadoCalculoFrete resultado) {
        transportesState.setValue(transporteMapper.mapFrom(resultado.transportes));
        freteState.setValue(new FreteUiState(resultado.precificacao.getValorTotal(), resultado.precificacao.getValorPorKg()));
        freteCalculado.setValue(true);
    }

    private static final class ResultadoCalculoFrete {
        final List<Transporte> transportes;
        final PrecificacaoFrete precificacao;

        ResultadoCalculoFrete(List<Transporte> transportes, PrecificacaoFrete precificacao) {
            this.transportes = transportes;
            this.precificacao = precificacao;
        }
    }
}
