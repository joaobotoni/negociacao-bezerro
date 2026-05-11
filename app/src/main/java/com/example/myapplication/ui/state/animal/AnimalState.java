package com.example.myapplication.ui.state.animal;

public class AnimalState {
    private final EspecificacaoAnimalState especificacao;
    private final RacaState raca;
    private final CategoriaState categoria;
    public AnimalState(EspecificacaoAnimalState especificacao, RacaState raca, CategoriaState categoria) {
        this.especificacao = especificacao;
        this.raca = raca;
        this.categoria = categoria;
    }

    public EspecificacaoAnimalState getEspecificacao() {
        return especificacao;
    }

    public RacaState getRaca() {
        return raca;
    }
    public CategoriaState getCategoria() {
        return categoria;
    }
}
