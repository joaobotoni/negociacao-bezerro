package com.omni.negociacaobezerros.ui.viewmodels;

import android.location.Address;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.models.Rota;
import com.omni.negociacaobezerros.data.repositories.LocalizacaoRepository;
import com.omni.negociacaobezerros.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.RotaUiState;
import com.omni.negociacaobezerros.utils.mapper.RotaMapper;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RotaViewModel extends ViewModel {
    private final LocalizacaoRepository repositorio;
    private final RotaMapper mapper;
    private final TaskHelper taskHelper;
    private final MutableLiveData<RotaUiState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public RotaViewModel(LocalizacaoRepository repositorio, RotaMapper mapper, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
        this.taskHelper = taskHelper;
    }

    public LiveData<RotaUiState> getState() {
        return state;
    }

    public LiveData<Throwable> getErro() {
        return erro;
    }

    public void selecionar(Address origem, String destinoQuery) {
        taskHelper.execute(
                () -> calcularRota(origem, destinoQuery),
                state::setValue,
                erro::setValue
        );
    }

    public void limpar() {
        state.setValue(null);
    }

    private RotaUiState calcularRota(Address origem, String destinoQuery) throws Exception {
        Address destino = repositorio.findEndereco(destinoQuery).orElseThrow();
        Rota resposta = repositorio.calcularRota(origem, destino);
        return mapper.mapFrom(resposta);
    }
}