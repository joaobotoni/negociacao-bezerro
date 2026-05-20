package com.omni.negociacaobezerros.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.omni.negociacaobezerros.ui.state.animal.AnimalState;
import com.omni.negociacaobezerros.ui.state.animal.CategoriaState;
import com.omni.negociacaobezerros.ui.state.animal.RacaState;

import dagger.hilt.android.lifecycle.HiltViewModel;
import jakarta.inject.Inject;


@HiltViewModel
public class AnimalViewModel extends ViewModel {
    private final MutableLiveData<String> sexo = new MutableLiveData<>(null);
    private final MutableLiveData<Integer> idade = new MutableLiveData<>(null);
    private final MutableLiveData<RacaState> racaSelecionada = new MutableLiveData<>(null);
    private final MutableLiveData<CategoriaState> categoriaSelecionada = new MutableLiveData<>(null);
    private final MediatorLiveData<AnimalState> animalState = new MediatorLiveData<>();
    @Inject
    public AnimalViewModel() {
        animalState.addSource(sexo, s -> setState());
        animalState.addSource(idade, i -> setState());
        animalState.addSource(racaSelecionada, r -> setState());
        animalState.addSource(categoriaSelecionada, c -> setState());
    }

    public LiveData<AnimalState> getAnimalState() {
        return animalState;
    }
    public void setSexo(String newSexo) { sexo.setValue(newSexo); }
    public void setIdade(Integer newIdade) { idade.setValue(newIdade); }
    public void setRaca(RacaState raca) { racaSelecionada.setValue(raca); }
    public void setCategoria(CategoriaState categoria) { categoriaSelecionada.setValue(categoria); }

    private void setState() {
        AnimalState novoEstado = new AnimalState(
                sexo.getValue(), idade.getValue(),
                racaSelecionada.getValue(),
                categoriaSelecionada.getValue()
        );
        animalState.setValue(novoEstado);
    }

    public void limpar() {
        sexo.setValue(null);
        idade.setValue(null);
        racaSelecionada.setValue(null);
        categoriaSelecionada.setValue(null);
    }
}
