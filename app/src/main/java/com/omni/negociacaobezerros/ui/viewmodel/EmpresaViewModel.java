package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.EmpresaRepository;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.empresa.EmpresaState;
import com.omni.negociacaobezerros.utils.mappers.domain.EmpresaMapper;

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

    private final MutableLiveData<List<EmpresaState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);
    private final MutableLiveData<EmpresaState> selecionada = new MutableLiveData<>(null);

    @Inject
    public EmpresaViewModel(EmpresaRepository repositorio, EmpresaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
        carregar();
    }

    public LiveData<List<EmpresaState>> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }
    public LiveData<EmpresaState> getSelecionada() { return selecionada; }

    public void carregar() {
        taskHelper.execute(
                () -> repositorio.getAll().stream().map(mapper::mapFrom).collect(Collectors.toList()),
                state::setValue,
                erro::setValue
        );
    }

    public void selecionar(EmpresaState escolhida) {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new EmpresaState(
                        item.getId(),
                        item.getNome(),
                        Objects.equals(item.getId(),
                                escolhida.getId())))
                .collect(Collectors.toList()));
        selecionada.setValue(escolhida);
    }

    public void limparSelecao() {
        if (state.getValue() == null) return;
        state.setValue(state.getValue().stream()
                .map(item -> new EmpresaState(
                        item.getId(),
                        item.getNome(),
                        false))
                .collect(Collectors.toList()));
        selecionada.setValue(null);
    }
}