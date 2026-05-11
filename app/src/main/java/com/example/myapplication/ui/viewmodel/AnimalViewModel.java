package com.example.myapplication.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.myapplication.ui.helpers.TaskHelper;
import com.example.myapplication.ui.state.animal.AnimalState;
import com.example.myapplication.ui.state.animal.CategoriaState;
import com.example.myapplication.ui.state.animal.EspecificacaoAnimalState;
import com.example.myapplication.ui.state.animal.RacaState;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;

@HiltViewModel
public class AnimalViewModel extends ViewModel {
    private final TaskHelper taskHelper;
    private RacaState raca;
    private CategoriaState categoria;
    private EspecificacaoAnimalState especificacao;
    private final MutableLiveData<AnimalState> animalState = new MutableLiveData<>(null);
    private final MutableLiveData<Throwable> error = new MutableLiveData<>(null);

    @Inject
    public AnimalViewModel(TaskHelper taskHelper) {
        this.taskHelper = taskHelper;
    }

    public LiveData<AnimalState> getAnimalState() {
        return animalState;
    }

    public LiveData<Throwable> getError() {
        return error;
    }

    public void atualizarRaca(RacaState raca) {
        taskHelper.execute(() -> definirRaca(raca), animalState::postValue, error::postValue);
    }

    public void atualizarCategoria(CategoriaState categoria) {
        taskHelper.execute(() -> definirCategoria(categoria), animalState::postValue, error::postValue);
    }

    public void atualizarEspecificacao(String sexo, Integer idade) {
        taskHelper.execute(() -> definirEspecificacao(sexo, idade), animalState::postValue, error::postValue);
    }

    private AnimalState definirRaca(RacaState raca) {
        this.raca = raca;
        return build();
    }

    private AnimalState definirCategoria(CategoriaState categoria) {
        this.categoria = categoria;
        return build();
    }

    private AnimalState definirEspecificacao(String sexo, Integer idade) {
        this.especificacao = new EspecificacaoAnimalState(sexo, idade);
        return build();
    }

    private AnimalState build() {
        return new AnimalState(especificacao, raca, categoria);
    }
}