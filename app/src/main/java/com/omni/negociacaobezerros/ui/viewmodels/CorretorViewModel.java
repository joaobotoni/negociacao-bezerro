package com.omni.negociacaobezerros.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.synchronizable.CorretorRepository;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.CorretorUiState;
import com.omni.negociacaobezerros.utils.mapper.CorretorMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CorretorViewModel extends ViewModel {
    private final CorretorRepository repositorio;
    private final CorretorMapper mapper;
    private final TaskHelper taskHelper;
    private final MutableLiveData<List<CorretorUiState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);
    private final MutableLiveData<CorretorUiState> selecionado = new MutableLiveData<>(null);

    @Inject
    public CorretorViewModel(CorretorRepository repositorio, CorretorMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
    }

    public LiveData<List<CorretorUiState>> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }
    public LiveData<CorretorUiState> getSelecionado() { return selecionado; }

    public void carregar() {
        taskHelper.execute(
                () -> repositorio.getAll().stream()
                        .map(mapper::mapFrom)
                        .collect(Collectors.toList()),
                state::setValue,
                erro::setValue
        );
    }

    public void selecionar(CorretorUiState escolhido) {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new CorretorUiState(
                        item.getId(),
                        item.getNome(),
                        item.getComissao(),
                        item.getTipoComissao(),
                        Objects.equals(item.getId(),
                                escolhido.getId())))
                .collect(Collectors.toList()));
        selecionado.setValue(escolhido);
    }

    public void limpar() {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new CorretorUiState(
                        item.getId(),
                        item.getNome(),
                        item.getComissao(),
                        item.getTipoComissao(),
                        false))
                .collect(Collectors.toList()));
        selecionado.setValue(null);
    }
}
