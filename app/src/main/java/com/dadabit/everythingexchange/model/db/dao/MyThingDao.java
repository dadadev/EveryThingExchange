package com.dadabit.everythingexchange.model.db.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.dadabit.everythingexchange.model.db.entity.ThingEntity;

import java.util.List;

@Dao
public interface MyThingDao {

    @Query("SELECT * FROM things ORDER BY id DESC")
    List<ThingEntity> loadAllThings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ThingEntity> things);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertThing(ThingEntity thing);

    @Query("select * from things where id = :thingId")
    ThingEntity loadThing(int thingId);

    @Update
    void updateThing(ThingEntity thingEntity);


    @Delete
    void delete(ThingEntity thingEntity);

}
