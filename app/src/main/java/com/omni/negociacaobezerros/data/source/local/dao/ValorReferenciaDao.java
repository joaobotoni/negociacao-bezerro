package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoVeiculoFrete;
import com.omni.negociacaobezerros.data.source.local.entities.ValorReferencia;

import java.util.List;

@Dao
public interface ValorReferenciaDao extends AbstractDao<ValorReferencia> {
    @Override
    @Query("SELECT * FROM xgp_valor_referencia")
    List<ValorReferencia> getAll();

    @Query("SELECT * FROM xgp_valor_referencia WHERE id_valor_referencia = :id")
    ValorReferencia findById(long id);

    @Query("SELECT * FROM xgp_valor_referencia ORDER BY data_referencia DESC LIMIT 1")
    ValorReferencia findMaisRecente();

    @Override
    @Query("DELETE FROM xgp_valor_referencia")
    void deleteAll();
}