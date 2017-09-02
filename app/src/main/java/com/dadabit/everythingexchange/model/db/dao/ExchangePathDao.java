package com.dadabit.everythingexchange.model.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.dadabit.everythingexchange.model.db.entity.ExchangePathEntity;

import java.util.List;

@Dao
public interface ExchangePathDao {


    @Query("SELECT * FROM exchange_path")
    List<ExchangePathEntity> loadAllExchangePaths();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPath(ExchangePathEntity exchangePath);

    @Query("select * from exchange_path where exchangeId = :exchangeId LIMIT 1")
    ExchangePathEntity loadExchangePath(int exchangeId);
}
