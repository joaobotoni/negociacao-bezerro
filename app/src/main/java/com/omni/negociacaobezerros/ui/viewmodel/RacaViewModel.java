package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.RacaRepository;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.animal.RacaState;
import com.omni.negociacaobezerros.utils.mappers.domain.RacaMapper;

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

    private final MutableLiveData<List<RacaState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);
    private final MutableLiveData<RacaState> selecionada = new MutableLiveData<>(null);

    @Inject
    public RacaViewModel(RacaRepository repositorio, RacaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<RacaState>> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }
    public LiveData<RacaState> getSelecionada() { return selecionada; }

    public void carregar() {
        taskHelper.execute(
                () -> repositorio.getAll().stream().map(mapper::mapFrom).collect(Collectors.toList()),
                state::setValue,
                erro::setValue
        );
    }

    public void selecionar(RacaState escolhida) {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new RacaState(
                        item.getId(),
                        item.getDescricao(),
                        Objects.equals(item.getId(),
                                escolhida.getId())))
                .collect(Collectors.toList()));
        selecionada.setValue(escolhida);
    }
}