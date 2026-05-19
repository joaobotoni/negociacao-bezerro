package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.Transporte;
import com.omni.negociacaobezerros.data.repositories.FreteRepository;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.frete.StatusFrete;
import com.omni.negociacaobezerros.ui.state.frete.FreteState;
import com.omni.negociacaobezerros.utils.mappers.domain.PrecificacaoFreteMapper;

import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class PrecificacaoFreteViewModel extends ViewModel {

    private final FreteRepository repositorio;
    private final PrecificacaoFreteMapper mapper;
    private final TaskHelper taskHelper;

    private final MutableLiveData<FreteState> state = new MutableLiveData<>(null);
    private final MutableLiveData<BigDecimal> incidencia = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<Double> distancia = new MutableLiveData<>(0.0);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public PrecificacaoFreteViewModel(FreteRepository repositorio, PrecificacaoFreteMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
    }

    public LiveData<FreteState> getState() { return state; }
    public LiveData<BigDecimal> getIncidencia() { return incidencia; }
    public LiveData<Double> getDistancia() { return distancia; }
    public LiveData<Throwable> getErro() { return erro; }

    public void setDistancia(double value) {
        distancia.setValue(value);
    }

    public void calcularFrete(List<Transporte> transportes, double distancia, int cargaTotal, BigDecimal pesoMedio) {
        taskHelper.execute(
                () -> mapper.mapFrom(repositorio.calcularFrete(transportes, distancia, cargaTotal, pesoMedio)),
                state::setValue,
                erro::setValue
        );
    }

    public void calcularIncidencia(BigDecimal valorDoFrete, BigDecimal pesoMedio, int totalCarga) {
        taskHelper.execute(
                () -> repositorio.calcularIncidenciaFretePorKg(valorDoFrete, pesoMedio, totalCarga),
                resultado -> atualizarIncidencia(valorDoFrete, resultado),
                erro::setValue
        );
    }

    public void limpar() {
        state.setValue(null);
        incidencia.setValue(null);
    }

    private void atualizarIncidencia(BigDecimal valorDoFrete, BigDecimal resultado) {
        incidencia.setValue(resultado);
        state.setValue(new FreteState(valorDoFrete, resultado, StatusFrete.MANUAL));
    }
}