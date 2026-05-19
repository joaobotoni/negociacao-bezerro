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
    private final TaskHelper.Cancellables tarefas = new TaskHelper.Cancellables();
    private final MutableLiveData<List<CategoriaState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final MutableLiveData<CategoriaState> categoriaSelecionada = new MutableLiveData<>(null);

    @Inject
    public CategoriaViewModel(CategoriaFreteRepository repositorio, CategoriaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<CategoriaState>> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<CategoriaState> getCategoriaSelecionada() {
        return categoriaSelecionada;
    }

    public void selecionarCategoria(CategoriaState selecionada) {
        if (state.getValue() == null) return;

        List<CategoriaState> newList = state.getValue().stream()
                .map(item -> new CategoriaState(
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
        tarefas.adicionar(taskHelper.execute(
                () -> repositorio.getAll().stream()
                        .map(mapper::mapFrom)
                        .collect(Collectors.toList()),
                state::postValue,
                error::postValue
        ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        tarefas.cancelarTudo();
    }

    public void limparSelecao() {
        categoriaSelecionada.setValue(null);
        if (state.getValue() == null) return;
        List<CategoriaState> newList = state.getValue().stream()
                .map(item -> new CategoriaState(item.getId(), item.getDescricao(), false))
                .collect(Collectors.toList());
        state.setValue(newList);
    }
}
