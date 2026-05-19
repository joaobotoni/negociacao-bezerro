package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.TransporteRepository;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.state.frete.TransporteState;
import com.omni.negociacaobezerros.utils.mappers.domain.TransporteMapper;

import java.util.List;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class TransporteViewModel extends ViewModel {
    private final TransporteRepository repositorio;
    private final TransporteMapper mapper;
    private final TaskHelper taskHelper;
    private final MutableLiveData<List<TransporteState>> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public TransporteViewModel(TransporteRepository repositorio, TransporteMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
    }

    public LiveData<List<TransporteState>> getState() { return state; }
    public LiveData<Throwable> getErro() { return erro; }

    public void recomendar(long categoria, int quantidade) {
        taskHelper.execute(
                () -> mapper.mapFrom(repositorio.recomendarTransportes(categoria, quantidade)),
                state::setValue,
                erro::setValue
        );
    }
}