package com.omni.negociacaobezerros.data.source.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

@Entity(tableName = "empresa")
public class Empresa {
    @PrimaryKey(autoGenerate = true)
    @SerializedName("ID_EMPRESA")
    @ColumnInfo(name = "id_empresa")
    private int idEmpresa;
    @SerializedName("NOME")
    @ColumnInfo(name = "nome")
    private String nome;
    @SerializedName("LOCALIZACAO")
    @ColumnInfo(name = "localizacao")
    private String localizacao;

    public Empresa() {
    }

    public Empresa(int idEmpresa, String nome, String localizacao) {
        this.idEmpresa = idEmpresa;
        this.nome = nome;
        this.localizacao = localizacao;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public String getNome() {
        return nome;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }
}
