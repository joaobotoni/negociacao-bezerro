package com.omni.negociacaobezerros.data.source.local.contract;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Update;

import java.util.List;

public interface AbstractDao<T> {

    List<T> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(T t);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<T> t);

    @Update
    int update(T t);

    @Delete
    int delete(T t);

    void deleteAll();
}