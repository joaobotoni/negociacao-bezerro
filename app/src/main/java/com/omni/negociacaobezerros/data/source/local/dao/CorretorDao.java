package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.Corretor;

import java.util.List;

@Dao
public interface CorretorDao extends AbstractDao<Corretor> {
    @Override
    @Query("SELECT * FROM xgp_corretor")
    List<Corretor> getAll();

    @Query("SELECT * FROM xgp_corretor WHERE id_corretor = :id")
    Corretor findById(long id);

    @Override
    @Query("DELETE FROM xgp_corretor")
    void deleteAll();
}