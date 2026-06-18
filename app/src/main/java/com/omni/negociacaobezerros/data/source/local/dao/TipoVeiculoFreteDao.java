package com.omni.negociacaobezerros.data.source.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.omni.negociacaobezerros.data.source.local.contract.AbstractDao;
import com.omni.negociacaobezerros.data.source.local.entities.TipoVeiculoFrete;

import java.util.List;

@Dao
public interface TipoVeiculoFreteDao extends AbstractDao<TipoVeiculoFrete> {
    @Override
    @Query("SELECT * FROM xgp_tipo_veiculo_frete")
    List<TipoVeiculoFrete> getAll();
    @Query("SELECT * FROM xgp_tipo_veiculo_frete WHERE id_tipo_veiculo_frete = :id LIMIT 1")
    TipoVeiculoFrete findById(long id);

    @Override
    @Query("DELETE FROM xgp_tipo_veiculo_frete")
    void deleteAll();
}