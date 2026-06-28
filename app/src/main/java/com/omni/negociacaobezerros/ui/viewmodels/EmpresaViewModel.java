package com.omni.negociacaobezerros.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.synchronizable.EmpresaRepository;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.EmpresaUiState;
import com.omni.negociacaobezerros.utils.mapper.EmpresaMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class EmpresaViewModel extends ViewModel {
    private final EmpresaRepository repositorio;
    private final EmpresaMapper mapper;
    private final TaskHelper taskHelper;
    private final MutableLiveData<List<EmpresaUiState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);
    private final MutableLiveData<EmpresaUiState> selecionada = new MutableLiveData<>(null);

    @Inject
    public EmpresaViewModel(EmpresaRepository repositorio, EmpresaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
    }

    public LiveData<List<EmpresaUiState>> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }
    public LiveData<EmpresaUiState> getSelecionada() { return selecionada; }

    public void carregar() {
        taskHelper.execute(
                () -> repositorio.getAll().stream()
                        .map(mapper::mapFrom)
                        .collect(Collectors.toList()),
                state::setValue,
                erro::setValue
        );
    }

    public void selecionar(EmpresaUiState escolhida) {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new EmpresaUiState(
                        item.getId(),
                        item.getNome(),
                        item.getLocalizacao(),
                        Objects.equals(item.getId(),
                                escolhida.getId())))
                .collect(Collectors.toList()));
        selecionada.setValue(escolhida);
    }

    public void limparSelecao() {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new EmpresaUiState(
                        item.getId(),
                        item.getNome(),
                        item.getLocalizacao(),
                        false))
                .collect(Collectors.toList()));
        selecionada.setValue(null);
    }
}