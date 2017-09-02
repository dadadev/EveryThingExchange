package com.dadabit.everythingexchange.model.db.entity;


import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "exchange_path")
public class ExchangePathEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long exchangeId;
    private String exchangePath;

    public ExchangePathEntity(long exchangeId,
                              String exchangePath) {
        this.exchangeId = exchangeId;
        this.exchangePath = exchangePath;
    }

    public int getId() {
        return id;
    }

    public long getExchangeId() {
        return exchangeId;
    }

    public String getExchangePath() {
        return exchangePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setExchangeId(long exchangeId) {
        this.exchangeId = exchangeId;
    }

    public void setExchangePath(String exchangePath) {
        this.exchangePath = exchangePath;
    }
}
