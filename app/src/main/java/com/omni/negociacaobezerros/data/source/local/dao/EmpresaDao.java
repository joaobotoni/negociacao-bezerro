package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;


import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.Corretor;
import com.omni.negociacaobezerros.data.source.local.entities.Empresa;

import java.util.List;

@Dao
public interface EmpresaDao extends AbstractDao<Empresa> {
    @Override
    @Query("SELECT * FROM empresa")
    List<Empresa> getAll();

    @Query("SELECT * FROM empresa WHERE id_empresa = :id")
    Empresa findById(long id);
    @Override
    @Query("DELETE FROM empresa")
    void deleteAll();
}