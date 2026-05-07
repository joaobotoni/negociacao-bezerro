package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.repositories.CategoriaFreteRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.CategoriaUiState;
import com.example.myapplication.utils.mappers.domain.CategoriaMapper;

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
    private final MutableLiveData<List<CategoriaUiState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final MutableLiveData<CategoriaUiState> categoriaSelecionada = new MutableLiveData<>(null);

    @Inject
    public CategoriaViewModel(CategoriaFreteRepository repositorio, CategoriaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<CategoriaUiState>> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<CategoriaUiState> getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public void selecionarCategoria(CategoriaUiState selecionada) {
        if (state.getValue() == null) return;

        List<CategoriaUiState> newList = state.getValue().stream()
                .map(item -> new CategoriaUiState(
                        item.getId(),
                        item.getDescricao(),
                        Objects.equals(item.getId(), selecionada.getId())))
                .collect(Collectors.toList());

        state.setValue(newList);
        categoriaSelecionada.setValue(selecionada);
    }

    public void carregar() {
        listarCategorias();
    }

    private void listarCategorias() {
        taskHelper.execute(
                () -> repositorio.getAll().stream()
                        .map(mapper::mapFrom)
                        .collect(Collectors.toList()),
                state::postValue,
                error::postValue
        );
    }

    public void limparSelecao() {
        categoriaSelecionada.setValue(null);
        if (state.getValue() == null) return;
        List<CategoriaUiState> newList = state.getValue().stream()
                .map(item -> new CategoriaUiState(item.getId(), item.getDescricao(), false))
                .collect(Collectors.toList());
        state.setValue(newList);
    }
}
