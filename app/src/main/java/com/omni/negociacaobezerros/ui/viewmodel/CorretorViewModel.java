package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.CorretorRepository;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.empresa.CorretorState;
import com.omni.negociacaobezerros.utils.mappers.domain.CorretorMapper;

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

    private final MutableLiveData<List<CorretorState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);
    private final MutableLiveData<CorretorState> selecionado = new MutableLiveData<>(null);

    @Inject
    public CorretorViewModel(CorretorRepository repositorio, CorretorMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<CorretorState>> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }
    public LiveData<CorretorState> getSelecionado() { return selecionado; }

    public void carregar() {
        taskHelper.execute(
                () -> repositorio.getAll().stream().map(mapper::mapFrom).collect(Collectors.toList()),
                state::setValue,
                erro::setValue
        );
    }

    public void selecionar(CorretorState escolhido) {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new CorretorState(
                        item.getId(),
                        item.getNome(),
                        item.getComissao(),
                        item.getTipoComissao(),
                        Objects.equals(item.getId(),
                                escolhido.getId())))
                .collect(Collectors.toList()));
        selecionado.setValue(escolhido);
    }

    public void limparSelecao() {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new CorretorState(
                        item.getId(),
                        item.getNome(),
                        item.getComissao(),
                        item.getTipoComissao(),
                        false))
                .collect(Collectors.toList()));
        selecionado.setValue(null);
    }
}
