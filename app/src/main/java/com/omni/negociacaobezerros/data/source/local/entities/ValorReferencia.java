package com.omni.negociacaobezerros.data.source.local.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(
        tableName = "xgp_valor_referencia",
        foreignKeys = {
                @ForeignKey(
                        entity = TipoReferencia.class,
                        parentColumns = "id_tipo_referencia",
                        childColumns = "id_tipo_referencia",
                        onDelete = ForeignKey.RESTRICT
                ),
                @ForeignKey(
                        entity = Empresa.class,
                        parentColumns = "id_empresa",
                        childColumns = "id_empresa",
                        onDelete = ForeignKey.RESTRICT
                ),
        }
)
public class ValorReferencia {
    @PrimaryKey
    @ColumnInfo(name = "id_valor_referencia")
    @SerializedName("ID_VALOR_REFERENCIA")
    private int idValorReferencia;

    @ColumnInfo(name = "id_tipo_referencia")
    @SerializedName("ID_TIPO_REFERENCIA")
    private int idTipoReferencia;

    @ColumnInfo(name = "id_empresa")
    @SerializedName("ID_EMPRESA")
    private int idEmpresa;

    @ColumnInfo(name = "data_referencia")
    @SerializedName("DATA_REFERENCIA")
    private Date dataReferencia;

    @ColumnInfo(name = "valor_arroba_boi")
    @SerializedName("VALOR_ARROBA_BOI")
    private Double valorArrobaBoi;

    @ColumnInfo(name = "valor_bezerro")
    @SerializedName("VALOR_BEZERRO")
    private Double valorBezerro;

    @ColumnInfo(name = "peso_bezerro")
    @SerializedName("PESO_BEZERRO")
    private Integer pesoBezerro;

    @ColumnInfo(name = "perc_agio")
    @SerializedName("PERC_AGIO")
    private Double agioBezerro;

    @ColumnInfo(name = "perc_agio_ajustado")
    @SerializedName("PERC_AGIO_AJUSTADO")
    private Double agioAjustado;

    @ColumnInfo(name = "data_vencimento")
    @SerializedName("DATA_VENCIMENTO")
    private Date dataVencimento;

    @ColumnInfo(name = "sexo")
    @SerializedName("SEXO")
    private String sexo;

    public ValorReferencia() {
    }

    public ValorReferencia(int idValorReferencia, int idTipoReferencia, int idEmpresa, Date dataReferencia,
                           Double valorArrobaBoi, Double valorBezerro, Integer pesoBezerro, Double agioBezerro, Double agioAjustado) {
        this.idValorReferencia = idValorReferencia;
        this.idTipoReferencia = idTipoReferencia;
        this.idEmpresa = idEmpresa;
        this.dataReferencia = dataReferencia;
        this.valorArrobaBoi = valorArrobaBoi;
        this.valorBezerro = valorBezerro;
        this.pesoBezerro = pesoBezerro;
        this.agioBezerro = agioBezerro;
        this.agioAjustado = agioAjustado;
    }

    public int getIdValorReferencia() {
        return idValorReferencia;
    }

    public void setIdValorReferencia(int idValorReferencia) {
        this.idValorReferencia = idValorReferencia;
    }

    public int getIdTipoReferencia() {
        return idTipoReferencia;
    }

    public void setIdTipoReferencia(int idTipoReferencia) {
        this.idTipoReferencia = idTipoReferencia;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public Double getValorArrobaBoi() {
        return valorArrobaBoi;
    }

    public void setValorArrobaBoi(Double valorArrobaBoi) {
        this.valorArrobaBoi = valorArrobaBoi;
    }

    public Double getValorBezerro() {
        return valorBezerro;
    }

    public void setValorBezerro(Double valorBezerro) {
        this.valorBezerro = valorBezerro;
    }

    public Integer getPesoBezerro() {
        return pesoBezerro;
    }

    public void setPesoBezerro(Integer pesoBezerro) {
        this.pesoBezerro = pesoBezerro;
    }

    public Double getAgioBezerro() {
        return agioBezerro;
    }

    public void setAgioBezerro(Double agioBezerro) {
        this.agioBezerro = agioBezerro;
    }

    public Double getAgioAjustado() {
        return agioAjustado;
    }

    public void setAgioAjustado(Double agioAjustado) {
        this.agioAjustado = agioAjustado;
    }

    public Date getDataVencimento() {
        return dataVencimento;
    }

    public void setDataVencimento(Date dataVencimento) {
        this.dataVencimento = dataVencimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
}