package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.Raca;

import java.util.List;

@Dao
public interface RacaDao extends AbstractDao<Raca> {
    @Override
    @Query("SELECT * FROM xgp_raca")
    List<Raca> getAll();

    @Query("SELECT * FROM xgp_raca WHERE id_raca = :id")
    Raca findById(long id);

    @Override
    @Query("DELETE FROM xgp_raca")
    void deleteAll();
}
