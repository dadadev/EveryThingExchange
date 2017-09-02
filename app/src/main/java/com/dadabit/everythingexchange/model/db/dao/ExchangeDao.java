package com.dadabit.everythingexchange.model.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;

import java.util.List;

@Dao
public interface ExchangeDao {


//    @Query("SELECT * FROM exchanges ORDER BY id DESC, startDate DESC")
    @Query("SELECT * FROM exchanges")
    List<ExchangeEntity> loadAllExchanges();

    @Query("SELECT * FROM exchanges ORDER BY id DESC LIMIT 1")
    ExchangeEntity getLastExchange();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ExchangeEntity> exchanges);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertExchange(ExchangeEntity exchange);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateExchange(ExchangeEntity exchange);

    @Query("select * from exchanges where id = :exchangeId")
    ExchangeEntity loadExchange(int exchangeId);

    @Query("DELETE FROM exchanges") void deleteAll();

    @Delete
    void delete(ExchangeEntity exchange);


//    @Query("SELECT id FROM exchanges ORDER BY id DESC")
    @Query("SELECT id FROM exchanges")
    List<Integer> getIds();


}
