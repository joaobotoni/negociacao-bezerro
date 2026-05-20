package com.omni.negociacaobezerros.ui.state.animal;

public class AnimalState {
    private final String sexo;
    private final Integer idade;
    private final RacaState raca;
    private final CategoriaState categoria;

    public AnimalState(String sexo, Integer idade, RacaState raca, CategoriaState categoria) {
        this.sexo = sexo;
        this.idade = idade;
        this.raca = raca;
        this.categoria = categoria;
    }

    public String getSexo() {
        return sexo;
    }

    public Integer getIdade() {
        return idade;
    }

    public RacaState getRaca() {
        return raca;
    }

    public CategoriaState getCategoria() {
        return categoria;
    }
}
