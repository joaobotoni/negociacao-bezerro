package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.CategoriaFreteRepository;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.animal.CategoriaState;
import com.omni.negociacaobezerros.utils.mappers.domain.CategoriaMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class CategoriaViewModel extends ViewModel {

    private final CategoriaFreteRepository repositorio;
    private final CategoriaMapper mapper;
    private final TaskHelper taskHelper;

    private final MutableLiveData<List<CategoriaState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);
    private final MutableLiveData<CategoriaState> selecionada = new MutableLiveData<>(null);

    @Inject
    public CategoriaViewModel(CategoriaFreteRepository repositorio, CategoriaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<CategoriaState>> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }
    public LiveData<CategoriaState> getSelecionada() { return selecionada; }

    public void carregar() {
        taskHelper.execute(
                () -> repositorio.getAll().stream().map(mapper::mapFrom).collect(Collectors.toList()),
                state::setValue,
                erro::setValue
        );
    }

    public void selecionar(CategoriaState escolhida) {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new CategoriaState(
                        item.getId(),
                        item.getDescricao(),
                        Objects.equals(item.getId(),
                                escolhida.getId()))
                )
                .collect(Collectors.toList()));
        selecionada.setValue(escolhida);
    }

    public void limparSelecao() {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new CategoriaState(item.getId(), item.getDescricao(), false))
                .collect(Collectors.toList()));
        selecionada.setValue(null);
    }
}