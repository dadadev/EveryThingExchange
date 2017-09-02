package com.dadabit.everythingexchange.model.vo;


public class ChatMessage {

    private String message;
    private String uid;
    private long timeStamp;

    public ChatMessage(String message, String uid, long timeStamp) {
        this.message = message;
        this.uid = uid;
        this.timeStamp = timeStamp;
    }

    public ChatMessage() {
    }

    public String getMessage() {
        return message;
    }

    public String getUid() {
        return uid;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

}
