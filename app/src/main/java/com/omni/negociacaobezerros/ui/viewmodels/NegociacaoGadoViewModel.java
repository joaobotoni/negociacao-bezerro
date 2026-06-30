package com.omni.negociacaobezerros.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.synchronizable.EmpresaRepository;
import com.omni.negociacaobezerros.data.repositories.synchronizable.NegociacaoGadoRepository;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.RegistroNegociacaoUiState;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NegociacaoGadoViewModel extends ViewModel {
    private final NegociacaoGadoRepository negociacaoGadoRepository;
    private final EmpresaRepository empresaRepository;
    private final TaskHelper taskHelper;

    private final MutableLiveData<List<RegistroNegociacaoUiState>> historico = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public NegociacaoGadoViewModel(NegociacaoGadoRepository negociacaoGadoRepository, EmpresaRepository empresaRepository, TaskHelper taskHelper) {
        this.negociacaoGadoRepository = negociacaoGadoRepository;
        this.empresaRepository = empresaRepository;
        this.taskHelper = taskHelper;
    }

    public LiveData<List<RegistroNegociacaoUiState>> getHistorico() { return historico; }
    public LiveData<Throwable> getErro() { return erro; }

    public void carregar() {
        taskHelper.execute(this::buscarHistorico, historico::setValue, erro::setValue);
    }

    private List<RegistroNegociacaoUiState> buscarHistorico() {
        return negociacaoGadoRepository.getAll().stream()
                .map(this::mapToUiState)
                .collect(Collectors.toList());
    }

    private RegistroNegociacaoUiState mapToUiState(NegociacaoGado gado) {
        return empresaRepository.findById(gado.getIdEmpresa())
                .map(empresa -> new RegistroNegociacaoUiState(empresa.getNome(), empresa.getLocalizacao()))
                .orElseGet(() -> new RegistroNegociacaoUiState("", ""));
    }
}
