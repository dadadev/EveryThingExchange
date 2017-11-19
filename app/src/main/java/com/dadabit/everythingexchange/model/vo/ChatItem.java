package com.dadabit.everythingexchange.model.vo;



public class ChatItem {


    private int exchangeId;
    private String companionImg;
    private String companionName;

    private String myImg;
    private String myName;
    private String myThingImg;
    private String companionThingImg;

    private int messagesCounter;

    private int newMessages;
    private long lastUpdate;

    public ChatItem(int exchangeId,
                    String companionImg,
                    String companionName,
                    String myImg,
                    String myName,
                    String myThingImg,
                    String companionThingImg,
                    int messagesCounter) {
        this.exchangeId = exchangeId;
        this.companionImg = companionImg;
        this.companionName = companionName;
        this.myImg = myImg;
        this.myThingImg = myThingImg;
        this.myName = myName;
        this.companionThingImg = companionThingImg;
        this.messagesCounter = messagesCounter;
    }

    public int getExchangeId() {
        return exchangeId;
    }

    public String getCompanionImg() {
        return companionImg;
    }

    public String getCompanionName() {
        return companionName;
    }

    public String getMyImg() {
        return myImg;
    }

    public String getMyName() {
        return myName;
    }

    public String getMyThingImg() {
        return myThingImg;
    }

    public String getCompanionThingImg() {
        return companionThingImg;
    }

    public int getMessagesCounter() {
        return messagesCounter;
    }

    public int getNewMessages() {
        return newMessages;
    }


    public void setMessagesCounter(int messagesCounter) {
        this.messagesCounter = messagesCounter;
    }

    public void setNewMessages(int newMessages) {
        this.newMessages = newMessages;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
