package com.example.myapplication.ui.state;

import android.location.Address;

import java.util.Collections;
import java.util.List;

public class BuscaLocalizacaoState {
    private final List<Address> localizacoes;
    private final boolean carregando;
    public BuscaLocalizacaoState() {
        this.localizacoes = Collections.emptyList();
        this.carregando = false;
    }
    public BuscaLocalizacaoState(List<Address> localizacoes, boolean carregando) {
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