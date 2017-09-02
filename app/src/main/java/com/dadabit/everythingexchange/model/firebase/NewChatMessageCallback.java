package com.dadabit.everythingexchange.model.firebase;


import com.dadabit.everythingexchange.model.vo.ChatMessage;

public interface NewChatMessageCallback {

    void onNewMessage(int position, ChatMessage message);
}
