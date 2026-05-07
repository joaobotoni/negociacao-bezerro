package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.data.repositories.RacaRepository;
import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.CategoriaUiState;
import com.example.myapplication.ui.state.RacaUiState;
import com.example.myapplication.utils.mappers.domain.RacaMapper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RacaViewModel extends ViewModel {
    private final RacaRepository repositorio;
    private final RacaMapper mapper;
    private final TaskHelper taskHelper;
    private final MutableLiveData<List<RacaUiState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);
    private final MutableLiveData<RacaUiState> racaSelecionada = new MutableLiveData<>(null);

    @Inject
    public RacaViewModel(RacaRepository repositorio, RacaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<RacaUiState>> getState() {
        return state;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public LiveData<RacaUiState> getRacaSelecionada() {
        return racaSelecionada;
    }

    public void selecionarRaca(RacaUiState selecionada) {
        if (state.getValue() == null) return;

        List<RacaUiState> newList = state.getValue().stream()
                .map(item -> new RacaUiState(
                        item.getId(),
                        item.getDescricao(),
                        Objects.equals(item.getId(), selecionada.getId())))
                .collect(Collectors.toList());

        state.setValue(newList);
        racaSelecionada.setValue(selecionada);
    }

    public void carregar() {
        listarRacas();
    }

    private void listarRacas() {
        taskHelper.execute(
                () -> repositorio.getAll().stream()
                        .map(mapper::mapFrom)
                        .collect(Collectors.toList()),
                state::postValue,
                error::postValue
        );
    }

    public void limparSelecao() {
        racaSelecionada.setValue(null);
        if (state.getValue() == null) return;
        List<RacaUiState> newList = state.getValue().stream()
                .map(item -> new RacaUiState(item.getId(), item.getDescricao(), false))
                .collect(Collectors.toList());
        state.setValue(newList);
    }
}