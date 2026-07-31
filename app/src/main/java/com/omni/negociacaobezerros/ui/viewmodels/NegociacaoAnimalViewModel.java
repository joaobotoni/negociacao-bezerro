package com.omni.negociacaobezerros.ui.viewmodels;

import static com.omni.negociacaobezerros.utils.format.Decimals.ARREDONDAMENTO_PADRAO;
import static com.omni.negociacaobezerros.utils.format.Decimals.ESCALA_MONETARIA;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.ui.states.NegociacaoAnimalResumoUiState;
import com.omni.negociacaobezerros.ui.states.NegociacaoAnimalUiState;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NegociacaoAnimalViewModel extends ViewModel {
    private final MutableLiveData<List<NegociacaoAnimalUiState>> animaisState = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<BigDecimal> valorKgNegociado = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<Integer> quantidadeTotal = new MutableLiveData<>(0);
    private final MutableLiveData<NegociacaoAnimalResumoUiState> resumoState = new MutableLiveData<>(null);

    @Inject
    public NegociacaoAnimalViewModel() {
    }

    public LiveData<List<NegociacaoAnimalUiState>> getAnimaisState() { return animaisState; }
    public LiveData<NegociacaoAnimalResumoUiState> getResumoState() { return resumoState; }

    public void setQuantidadeTotal(Integer quantidadeInformada) {
        quantidadeTotal.setValue(quantidadeInformada != null ? quantidadeInformada : 0);
        publicarResumo();
    }

    public void setValorKgNegociado(BigDecimal valorKgInformado) {
        valorKgNegociado.setValue(orZero(valorKgInformado));
    }

    public void adicionar(double pesoInformado) {
        List<NegociacaoAnimalUiState> lista = new ArrayList<>(obterListaAtual());
        BigDecimal valorKg = orZero(valorKgNegociado.getValue());
        BigDecimal valorCabeca = arredondarMonetario(valorKg.multiply(BigDecimal.valueOf(pesoInformado)));
        lista.add(new NegociacaoAnimalUiState(proximoId(lista), pesoInformado, valorKg, valorCabeca));
        publicarLista(lista);
    }

    public void editar(int id, double novoPesoInformado) {
        List<NegociacaoAnimalUiState> lista = new ArrayList<>(obterListaAtual());
        BigDecimal valorKg = orZero(valorKgNegociado.getValue());
        BigDecimal valorCabeca = arredondarMonetario(valorKg.multiply(BigDecimal.valueOf(novoPesoInformado)));
        substituir(lista, new NegociacaoAnimalUiState(id, novoPesoInformado, valorKg, valorCabeca));
        publicarLista(lista);
    }

    public void remover(int id) {
        List<NegociacaoAnimalUiState> lista = new ArrayList<>(obterListaAtual());
        lista.removeIf(animal -> animal.getId() == id);
        publicarLista(lista);
    }

    public void limpar() {
        animaisState.setValue(new ArrayList<>());
        valorKgNegociado.setValue(BigDecimal.ZERO);
        quantidadeTotal.setValue(0);
        resumoState.setValue(null);
    }

    private void substituir(List<NegociacaoAnimalUiState> lista, NegociacaoAnimalUiState animalEditado) {
        for (int i = 0; i < lista.size(); i++) {
            if (lista.get(i).getId() != animalEditado.getId()) continue;
            lista.set(i, animalEditado);
            return;
        }
    }

    private void publicarLista(List<NegociacaoAnimalUiState> lista) {
        animaisState.setValue(lista);
        publicarResumo();
    }

    private void publicarResumo() {
        List<NegociacaoAnimalUiState> lista = obterListaAtual();
        int total = quantidadeTotal.getValue() != null ? quantidadeTotal.getValue() : 0;
        int progresso = lista.size();
        int percentual = total > 0 ? Math.min(100, progresso * 100 / total) : 0;
        int faltam = Math.max(0, total - progresso);
        resumoState.setValue(new NegociacaoAnimalResumoUiState(
                total, progresso, percentual, faltam,
                calcularPesoMedio(lista), calcularValorTotal(lista), progresso > 0 && progresso >= total));
    }

    private double calcularPesoMedio(List<NegociacaoAnimalUiState> lista) {
        if (lista.isEmpty()) return 0.0;
        return lista.stream().mapToDouble(NegociacaoAnimalUiState::getPeso).average().orElse(0.0);
    }

    private BigDecimal calcularValorTotal(List<NegociacaoAnimalUiState> lista) {
        BigDecimal total = BigDecimal.ZERO;
        for (NegociacaoAnimalUiState animal : lista) total = total.add(animal.getValorPorCabeca());
        return arredondarMonetario(total);
    }

    private int proximoId(List<NegociacaoAnimalUiState> lista) {
        return lista.stream().mapToInt(NegociacaoAnimalUiState::getId).max().orElse(0) + 1;
    }

    private List<NegociacaoAnimalUiState> obterListaAtual() {
        List<NegociacaoAnimalUiState> atual = animaisState.getValue();
        return atual != null ? atual : new ArrayList<>();
    }

    private BigDecimal arredondarMonetario(BigDecimal valor) {
        return valor.setScale(ESCALA_MONETARIA, ARREDONDAMENTO_PADRAO);
    }

    private BigDecimal orZero(BigDecimal valor) {
        return valor != null ? valor : BigDecimal.ZERO;
    }
}
