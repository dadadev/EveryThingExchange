package com.dadabit.everythingexchange.model.firebase;


import com.dadabit.everythingexchange.model.vo.User;

public interface LoadFireBaseUserCallback {

    void onLoaded(User fireBaseUser);

}
