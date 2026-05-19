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
    private final TaskHelper taskHelper;
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final PrecificacaoFreteMapper precificacaoFreteMapper;
    private final MutableLiveData<FreteState> state = new MutableLiveData<>(null);
    private final MutableLiveData<BigDecimal> incidencia = new MutableLiveData<>(BigDecimal.ZERO);
    private final MutableLiveData<Double> distancia = new MutableLiveData<>(0.0);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);

    @Inject
    public PrecificacaoFreteViewModel(
            TaskHelper taskHelper,
            FreteRepository repositorio,
            PrecificacaoFreteMapper precificacaoFreteMapper
    ) {
        this.taskHelper = taskHelper;
        this.repositorio = repositorio;
        this.precificacaoFreteMapper = precificacaoFreteMapper;
    }

    public LiveData<FreteState> getState() {
        return state;
    }

    public LiveData<BigDecimal> getIncidencia() {
        return incidencia;
    }

    public LiveData<Double> getDistancia() {
        return distancia;
    }

    public LiveData<Throwable> getError() {
        return error;
    }
    public void setDistancia(double value) {
        distancia.setValue(value);
    }

    public void calcularFrete(List<Transporte> transportes, double distancia, int cargaTotal, BigDecimal pesoMedio) {
        tarefas.adicionar(taskHelper.execute(
                () -> precificacaoFreteMapper.mapFrom(repositorio.calcularFrete(transportes, distancia, cargaTotal, pesoMedio)),
                state::postValue,
                error::postValue
        ));
    }

    public void calcularIncidencia(BigDecimal valorDoFrete, BigDecimal pesoMedio, int totalCarga) {
        tarefas.adicionar(taskHelper.execute(
                () -> repositorio.calcularIncidenciaFretePorKg(valorDoFrete, pesoMedio, totalCarga),
                resultadoIncidencia -> {
                    incidencia.setValue(resultadoIncidencia);
                    state.setValue(new FreteState(valorDoFrete, resultadoIncidencia, StatusFrete.MANUAL));
                },
                error::postValue
        ));
    }

    public void limpar() {
        state.setValue(null);
        incidencia.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tarefas.cancelarTudo();
    }
}
