package com.dadabit.everythingexchange.utils;


import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.dadabit.everythingexchange.model.db.entity.ExchangeEntity;
import com.dadabit.everythingexchange.model.vo.ChatItem;
import com.dadabit.everythingexchange.model.vo.ChatMessage;
import com.dadabit.everythingexchange.model.vo.ChatMessageItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatItemsManager {

    private String uid;
    private int currentChatId = -1;

    private int newMessages;

    private List<ChatItem> chatItems;

    private SparseArray<List<ChatMessageItem>> messagesMap;

    private SparseArray<List<ChatMessage>> fireBaseMessages;


    private MessagesCounterListener newMessagesCallback;
    private ChatsAdapterCallback chatsAdapterCallback;
    private NewMessageCallback singleChatCallback;

    private Calendar previousDate;
    private Calendar currentDate;

    private SimpleDateFormat sdf;
    private SimpleDateFormat timeFormat;



    public ChatItemsManager(String uid) {
        this.uid = uid;
    }

    public void initChatItems(List<ExchangeEntity> exchanges,
                              int[] messagesCounters){
        Log.d("@@@", "ChatItemsManager.initChatItems");

        chatItems = new ArrayList<>();
        messagesMap = new SparseArray<>();
        fireBaseMessages = new SparseArray<>();

        previousDate = Calendar.getInstance();
        currentDate = Calendar.getInstance();

        sdf = new SimpleDateFormat("dd MMMM yyyy (EEEE) ", Locale.getDefault());
        timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());


        for (int i = 0; i < exchanges.size(); i++) {

            chatItems.add(new ChatItem(
                    exchanges.get(i).getId(),
                    exchanges.get(i).getThing2_ownerImg(),
                    exchanges.get(i).getThing2_ownerName(),
                    exchanges.get(i).getThing1_ownerImg(),
                    exchanges.get(i).getThing1_ownerName(),
                    exchanges.get(i).getThing1_img(),
                    exchanges.get(i).getThing2_img(),
                    messagesCounters[i]));

            fireBaseMessages.put(
                    exchanges.get(i).getId(),
                    new ArrayList<ChatMessage>());
        }

    }

    public void addMessage(int id, ChatMessage fireBaseMessage){

        if (fireBaseMessages.get(id) == null){
            fireBaseMessages.put(id, new ArrayList<ChatMessage>());

        }
        fireBaseMessages.get(id).add(fireBaseMessage);

        convertChatMessage(id);
        
        updateChatItem(id);



    }

    private void updateChatItem(int id) {

        for (int i = 0; i < chatItems.size(); i++) {

            if (chatItems.get(i).getExchangeId() == id){

                ChatItem chatItem = chatItems.get(i);

                chatItem.setNewMessages(
                        fireBaseMessages.get(id).size() - chatItems.get(i).getMessagesCounter());

                chatItem.setLastUpdate(
                        fireBaseMessages.get(id).get(
                                fireBaseMessages.get(id).size()-1)
                                .getTimeStamp());

                chatItems.remove(i);
                chatItems.add(0, chatItem);

                updateNewMessages();
            }

        }


        
    }

    public void updateNewMessages() {

        int counter = 0;

        for (ChatItem chatItem :
                chatItems) {
            counter += chatItem.getNewMessages();
        }

        newMessages = counter;

        if (newMessagesCallback != null){
            newMessagesCallback.onCounterChange(newMessages);
        }
        if (chatsAdapterCallback != null){
            chatsAdapterCallback.onChatsChange(chatItems);
        }

    }

    private int lastMessageOwner;
    private void convertChatMessage(int id) {

        currentDate.setTimeInMillis(
                fireBaseMessages.get(id).get(fireBaseMessages.get(id).size()-1).getTimeStamp());

        previousDate.setTimeInMillis(
                fireBaseMessages.get(id).size() > 1
                        ? fireBaseMessages.get(id).get(
                        fireBaseMessages.get(id).size() - 2).getTimeStamp()
                        : 0);

        if (Utils.isBeforeDay(previousDate, currentDate)){

            addNewMessageItem(
                    id,
                    new ChatMessageItem(
                            "",
                            ChatMessageItem.DATE,
                            sdf.format(currentDate.getTime())));

        }

        final int currentOwner =
                uid.equals(
                        fireBaseMessages
                                .get(id)
                                .get(fireBaseMessages.get(id).size()-1)
                                .getUid())
                        ? ChatMessageItem.SENDER : ChatMessageItem.RECIPIENT;

        if (lastMessageOwner != currentOwner){
            lastMessageOwner = currentOwner;

            switch (currentOwner){
                case ChatMessageItem.RECIPIENT:

                    addNewMessageItem(
                            id,
                            new ChatMessageItem(
                                    chatItems.get(getChatItemPosition(id)).getCompanionImg(),
                                    ChatMessageItem.AVATAR_RECIPIENT,
                                    chatItems.get(getChatItemPosition(id)).getCompanionName()));

                    break;
                case ChatMessageItem.SENDER:

                    addNewMessageItem(
                            id,
                            new ChatMessageItem(
                                    chatItems.get(getChatItemPosition(id)).getMyImg(),
                                    ChatMessageItem.AVATAR_SENDER,
                                    chatItems.get(getChatItemPosition(id)).getMyName()));

                    break;
            }

        }


        addNewMessageItem(
                id,
                new ChatMessageItem(
                        fireBaseMessages.get(id).get(fireBaseMessages.get(id).size()-1).getMessage(),
                        currentOwner,
                        timeFormat.format(currentDate.getTime())));

    }


    private void addNewMessageItem(int id, ChatMessageItem chatMessageItem) {

        if (messagesMap.get(id) == null){
            messagesMap.put(id, new ArrayList<ChatMessageItem>());

        }

        messagesMap.get(id).add(chatMessageItem);

        if (singleChatCallback != null && id == currentChatId){
            singleChatCallback.onNewMessage(
                    messagesMap.get(id).get(messagesMap.get(id).size()-1));
        }

    }

    private int getChatItemPosition(int id) {
        for (int i = 0; i < chatItems.size(); i++) {
            if (chatItems.get(i).getExchangeId() == id){
                return i;
            }
        }
        return 0;
    }


    public void addChatItem (ChatItem newChatItem){
        chatItems.add(newChatItem);
    }


    public void removeChatItem(int id) {

        for (int i = 0; i < chatItems.size(); i++) {
            if (chatItems.get(i).getExchangeId() == id){

                chatItems.remove(i);
                messagesMap.remove(id);
                fireBaseMessages.remove(id);

                if (currentChatId == id
                        && singleChatCallback != null){
                    currentChatId = -1;

                    singleChatCallback.onChatRemoved();
                }

                updateNewMessages();

                break;
            }
        }


    }

    public int cleanNewMessagesCounter(int id){

        for (int i = 0; i < chatItems.size(); i++) {
            if (chatItems.get(i).getExchangeId() == id) {

                newMessages -= chatItems.get(i).getNewMessages();
                chatItems.get(i).setMessagesCounter(fireBaseMessages.get(id).size());
                chatItems.get(i).setNewMessages(0);

                if (chatsAdapterCallback != null){
                    chatsAdapterCallback.onChatsChange(chatItems);
                }

                if (newMessagesCallback != null){
                    newMessagesCallback.onCounterChange(newMessages);
                }


            }
        }

        return fireBaseMessages.get(id) == null ? -1 :fireBaseMessages.get(id).size();


    }


    public void changeChatEndDate(int id, long endDate) {
        Log.d("@@@", "ChatItemsManager.changeChatEndDate");

        if (currentChatId == id && singleChatCallback != null){
            singleChatCallback.onDateChanged(endDate);
        }

    }

    public List<ChatMessageItem> getMessageItems(int id){

        return messagesMap.get(id);

    }

    public List<ChatItem> getChatItems() {
        return chatItems;
    }

    public void attachNewMessagesListener(MessagesCounterListener newMessagesCallback){
        this.newMessagesCallback = newMessagesCallback;
        this.newMessagesCallback.onCounterChange(newMessages);
    }
    public void detachNewMessagesListener(){
        newMessagesCallback = null;
    }

    public void attachChatsAdapterListener(ChatsAdapterCallback chatsAdapterCallback){
        Log.d("@@@", "ChatItemsManager.attachChatsAdapterListener");
        this.chatsAdapterCallback = chatsAdapterCallback;
        this.chatsAdapterCallback.onChatsChange(chatItems);
    }
    public void detachChatsAdapterListener(){
        Log.d("@@@", "ChatItemsManager.detachChatsAdapterListener");
        chatsAdapterCallback = null;
    }

    public void attachSingleChatListener(int id, NewMessageCallback singleChatCallback){

        currentChatId = id;
        this.singleChatCallback = singleChatCallback;


    }
    public void detachSingleChatListener(){
        currentChatId = -1;
        singleChatCallback = null;
    }

    public void loadChatItems(int id) {

        if (singleChatCallback != null){
            singleChatCallback.onMessagesLoad(messagesMap.get(id));
        }

    }


    public interface MessagesCounterListener {
        void onCounterChange(int counter);
    }

    public interface ChatsAdapterCallback {
        void onChatsChange(List<ChatItem> changedChatItems);
    }

    public interface NewMessageCallback {
        void onMessagesLoad(List<ChatMessageItem> messages);
        void onNewMessage(ChatMessageItem message);
        void onDateChanged(long newDate);
        void onChatRemoved();
    }


}
