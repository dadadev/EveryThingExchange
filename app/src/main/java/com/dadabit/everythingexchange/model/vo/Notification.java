package com.dadabit.everythingexchange.model.vo;


public class Notification {

    private long timestamp;
    private String body;
    private String from;
    private String to;

    public Notification(long timestamp, String body, String from, String to) {
        this.timestamp = timestamp;
        this.body = body;
        this.from = from;
        this.to = to;
    }

    public Notification() {
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getBody() {
        return body;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }
}