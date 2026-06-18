package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.CapacidadeFrete;

import java.util.List;

@Dao
public interface CapacidadeFreteDao extends AbstractDao<CapacidadeFrete> {

    @Override
    @Query("SELECT * FROM xgp_capacidade_frete")
    List<CapacidadeFrete> getAll();

    @Query("SELECT * FROM xgp_capacidade_frete WHERE id_capacidade_frete = :id")
    CapacidadeFrete findById(long id);

    @Query("SELECT * FROM xgp_capacidade_frete WHERE id_categoria_frete = :id_categoria_frete ORDER BY qtde_final DESC")
    List<CapacidadeFrete> findByCategoria(long id_categoria_frete);
    @Override
    @Query("DELETE FROM xgp_capacidade_frete")
    void deleteAll();
}