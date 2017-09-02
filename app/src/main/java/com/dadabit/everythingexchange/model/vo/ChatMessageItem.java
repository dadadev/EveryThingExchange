package com.dadabit.everythingexchange.model.vo;


public class ChatMessageItem {


    public static final int DATE = 0;
    public static final int SENDER = 1;
    public static final int RECIPIENT = 2;
    public static final int AVATAR_RECIPIENT = 3;
    public static final int AVATAR_SENDER = 4;



    private String message;
    private int type;
    private String time;

    public ChatMessageItem(String message, int type, String time) {
        this.message = message;
        this.type = type;
        this.time = time;
    }

    public String getMessage() {
        return message;
    }

    public int getType() {
        return type;
    }

    public String getTime() {
        return time;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
