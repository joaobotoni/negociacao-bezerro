package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoReferencia;

import java.util.List;

@Dao
public interface TipoReferenciaDao extends AbstractDao<TipoReferencia> {
    @Override
    @Query("SELECT * FROM xgp_tipo_referencia")
    List<TipoReferencia> getAll();

    @Query("SELECT * FROM xgp_tipo_referencia WHERE id_tipo_referencia = :id")
    TipoReferencia findById(long id);

    @Override
    @Query("DELETE FROM xgp_tipo_referencia")
    void deleteAll();
}