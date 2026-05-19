package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.PrecificacaoBezerro;
import com.omni.negociacaobezerros.data.repositories.PrecificacaoBezerroRepository;
import com.omni.negociacaobezerros.data.repositories.ValorReferenciaRepository;
import com.omni.negociacaobezerros.domain.implementation.PrecificacaoBezerroImplementation;
import com.omni.negociacaobezerros.domain.strategy.PrecificacaoBezerroComFrete;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.negociacao.CotacaoState;

import java.math.BigDecimal;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class SimulacaoViewModel extends ViewModel {

    private final PrecificacaoBezerroRepository precificacaoBezerroRepository;
    private final ValorReferenciaRepository valorReferenciaRepository;
    private final TaskHelper taskHelper;

    private final MutableLiveData<CotacaoState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public SimulacaoViewModel(PrecificacaoBezerroRepository precificacaoBezerroRepository,
                              ValorReferenciaRepository valorReferenciaRepository,
                              TaskHelper taskHelper) {
        this.precificacaoBezerroRepository = precificacaoBezerroRepository;
        this.valorReferenciaRepository = valorReferenciaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<CotacaoState> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }

    public void processarCotacao(BigDecimal peso, Integer quantidade) {
        taskHelper.execute(
                () -> calcularCotacao(peso, quantidade),
                state::setValue,
                erro::setValue
        );
    }

    public void limpar() {
        state.setValue(null);
    }

    private CotacaoState calcularCotacao(BigDecimal peso, Integer quantidade) {
        PrecificacaoBezerro p = precificarBezerroComFrete(peso, quantidade);
        return new CotacaoState(p.getValorPorKg(), p.getValorPorCabeca(), quantidade, p.getValorTotal());
    }

    private PrecificacaoBezerro precificarBezerroComFrete(BigDecimal peso, Integer quantidade) {
        return new PrecificacaoBezerroImplementation(
                new PrecificacaoBezerroComFrete(precificacaoBezerroRepository),
                valorReferenciaRepository
        ).executar(peso, quantidade);
    }
}