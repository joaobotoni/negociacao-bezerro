package com.example.myapplication.ui.state;

public class CategoriaState {
    private final int id;
    private final String descricao;
    private final boolean isSelected;

    public CategoriaState(int id, String descricao, boolean isSelected) {
        this.id = id;
        this.descricao = descricao;
        this.isSelected = isSelected;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
