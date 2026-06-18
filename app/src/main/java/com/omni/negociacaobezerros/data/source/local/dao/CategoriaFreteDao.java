package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.CategoriaFrete;

import java.util.List;

@Dao
public interface CategoriaFreteDao extends AbstractDao<CategoriaFrete> {
    @Override
    @Query("SELECT * FROM xgp_categoria_frete ORDER BY descricao")
    List<CategoriaFrete> getAll();
    @Query("SELECT * FROM xgp_categoria_frete WHERE id_categoria_frete = :id")
    CategoriaFrete findById(long id);
    @Override
    @Query("DELETE FROM xgp_categoria_frete")
    void deleteAll();
}