package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaNegociacao;

import java.util.List;

@Dao
public interface CategoriaNegociacaoDao extends AbstractDao<CategoriaNegociacao> {
    @Override
    @Query("SELECT * FROM xgp_categoria_neg")
    List<CategoriaNegociacao> getAll();

    @Query("SELECT * FROM xgp_categoria_neg WHERE id_categoria_neg = :id")
    CategoriaNegociacao findById(long id);

    @Override
    @Query("DELETE FROM xgp_categoria_neg")
    void deleteAll();
}