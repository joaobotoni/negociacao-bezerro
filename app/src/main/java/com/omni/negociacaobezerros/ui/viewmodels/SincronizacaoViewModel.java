package com.omni.negociacaobezerros.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.SynchronizationRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.EmpresaRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.NegociacaoGadoRepository;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.NegociacaoUiState;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class SincronizacaoViewModel extends ViewModel {
    private static final String STATUS_PENDENTE = "PENDENTE";
    private static final String STATUS_SINCRONIZADO = "SINCRONIZADO";

    private final NegociacaoGadoRepository negociacaoGadoRepository;
    private final EmpresaRepository empresaRepository;
    private final SynchronizationRepository synchronizationRepository;
    private final TaskHelper taskHelper;

    private final MutableLiveData<List<NegociacaoUiState>> negociacoesState = new MutableLiveData<>(null);
    private final MutableLiveData<Boolean> sincronizando = new MutableLiveData<>(false);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public SincronizacaoViewModel(NegociacaoGadoRepository negociacaoGadoRepository, EmpresaRepository empresaRepository,
                                   SynchronizationRepository synchronizationRepository, TaskHelper taskHelper) {
        this.negociacaoGadoRepository = negociacaoGadoRepository;
        this.empresaRepository = empresaRepository;
        this.synchronizationRepository = synchronizationRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<List<NegociacaoUiState>> getNegociacoesState() { return negociacoesState; }
    public LiveData<Boolean> getSincronizando() { return sincronizando; }
    public LiveData<Throwable> getErro() { return erro; }

    public void carregar() {
        taskHelper.execute(this::buscarNegociacoesPendentes, negociacoesState::setValue, erro::setValue);
    }

    public void sincronizar() {
        sincronizando.setValue(true);
        synchronizationRepository.synchronize(this::onSincronizacaoCompleta, this::onSincronizacaoFalhou);
    }

    private List<NegociacaoUiState> buscarNegociacoesPendentes() {
        return negociacaoGadoRepository.getAll().stream()
                .filter(this::isPendente)
                .map(this::mapToUiState)
                .collect(Collectors.toList());
    }

    private boolean isPendente(NegociacaoGado gado) {
        return STATUS_PENDENTE.equals(gado.getStatus());
    }

    private NegociacaoUiState mapToUiState(NegociacaoGado gado) {
        String nomeEmpresa = empresaRepository.findById(gado.getIdEmpresa()).map(empresa -> empresa.getNome()).orElse("");
        int quantidade = gado.getQtdeAnimais() != null ? gado.getQtdeAnimais() : 0;
        return new NegociacaoUiState(nomeEmpresa, quantidade);
    }

    private void onSincronizacaoCompleta() {
        taskHelper.execute(this::marcarPendentesComoSincronizados, ignorado -> recarregarAposSincronizar(), erro::setValue);
    }

    private void onSincronizacaoFalhou(Throwable throwable) {
        sincronizando.setValue(false);
        erro.setValue(throwable);
    }

    private Void marcarPendentesComoSincronizados() {
        for (NegociacaoGado gado : negociacaoGadoRepository.getAll()) {
            if (!isPendente(gado)) continue;
            gado.setStatus(STATUS_SINCRONIZADO);
            negociacaoGadoRepository.update(gado);
        }
        return null;
    }

    private void recarregarAposSincronizar() {
        sincronizando.setValue(false);
        carregar();
    }
}
