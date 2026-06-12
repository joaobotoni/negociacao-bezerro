package com.omni.negociacaobezerros.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.data.repositories.LocalizacaoRepository;
import com.omni.negociacaobezerros.ui.helpers.TaskHelper;
import com.omni.negociacaobezerros.ui.states.LocalizacaoUiState;

import java.io.IOException;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LocalizacaoViewModel extends ViewModel {

    private final LocalizacaoRepository repositorio;
    private final TaskHelper taskHelper;

    private final MutableLiveData<LocalizacaoUiState> state = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> erro = new MutableLiveData<>(null);

    @Inject
    public LocalizacaoViewModel(LocalizacaoRepository repositorio, TaskHelper taskHelper) {
        this.repositorio = repositorio;
        this.taskHelper = taskHelper;
    }

    public LiveData<LocalizacaoUiState> getState() {
        return state;
    }

    public LiveData<Throwable> getErro() {
        return erro;
    }

    public void buscar(String consulta, double latitude, double longitude) {
        taskHelper.execute(
                () -> carregarLocalizacoes(consulta, latitude, longitude),
                state::setValue,
                erro::setValue
        );
    }

    private LocalizacaoUiState carregarLocalizacoes(String consulta, double latitude, double longitude) throws Exception {
        String codigoPais = obterCodigoPais(latitude, longitude);
        return new LocalizacaoUiState(repositorio.findEnderecos(consulta, codigoPais), false);
    }

    private String obterCodigoPais(double latitude, double longitude) throws IOException {
        return repositorio.getCodigoPais(latitude, longitude)
                .orElseThrow(() -> new RuntimeException("Código do país não encontrado"));
    }
}