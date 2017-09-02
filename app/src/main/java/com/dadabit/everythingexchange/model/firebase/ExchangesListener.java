package com.dadabit.everythingexchange.model.firebase;


import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.google.firebase.database.DataSnapshot;

public interface ExchangesListener {

    void onNew(ExchangeEntity newExchange);

    void onDateChange(String fireBaseId, long newDate);

    void onCancelExchange(String fireBaseId);

    void onEndExchange(String fireBaseId);

}
