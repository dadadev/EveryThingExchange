package com.dadabit.everythingexchange.model.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.dadabit.everythingexchange.model.db.converter.DateConverter;
import com.dadabit.everythingexchange.model.db.converter.ImageConverter;
import com.dadabit.everythingexchange.model.db.dao.ExchangeDao;
import com.dadabit.everythingexchange.model.db.dao.ExchangePathDao;
import com.dadabit.everythingexchange.model.db.dao.MyThingDao;
import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.db.entity.ExchangePathEntity;
import com.dadabit.everythingexchange.model.db.entity.ThingEntity;

@Database(entities = {
        ThingEntity.class,
        ExchangeEntity.class,
        ExchangePathEntity.class},
        version = 1)


@TypeConverters({
        DateConverter.class,
        ImageConverter.class})

public abstract class AppDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "db_things_exchange";

    private static AppDatabase INSTANCE;

    public abstract MyThingDao myThingDao();
    public abstract ExchangeDao exchangeDao();
    public abstract ExchangePathDao exchangePathDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, DATABASE_NAME).build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

}
