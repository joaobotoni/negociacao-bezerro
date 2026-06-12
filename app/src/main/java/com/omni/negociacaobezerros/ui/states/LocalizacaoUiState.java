package com.omni.negociacaobezerros.ui.states;

import android.location.Address;

import java.util.Collections;
import java.util.List;

public class LocalizacaoUiState {
    private final List<Address> localizacoes;
    private final boolean carregando;
    public LocalizacaoUiState() {
        this.localizacoes = Collections.emptyList();
        this.carregando = false;
    }

    public LocalizacaoUiState(List<Address> localizacoes, boolean carregando) {
        this.localizacoes = localizacoes;
        this.carregando = carregando;
    }

    public List<Address> getLocalizacoes() {
        return localizacoes;
    }

    public boolean isCarregando() {
        return carregando;
    }
}
