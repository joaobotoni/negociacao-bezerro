package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.NegociacaoGado;

import java.util.List;

@Dao
public interface NegociacaoGadoDao extends AbstractDao<NegociacaoGado> {
    @Override
    @Query("SELECT * FROM xgp_negociacao_gado")
    List<NegociacaoGado> getAll();
    @Query("SELECT * FROM xgp_negociacao_gado WHERE id_negociacao_gado = :id")
    NegociacaoGado findById(long id);
    @Override
    @Query("DELETE FROM xgp_negociacao_gado")
    void deleteAll();
}