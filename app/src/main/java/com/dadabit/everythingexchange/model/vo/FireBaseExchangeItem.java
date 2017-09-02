package com.dadabit.everythingexchange.model.vo;


public class FireBaseExchangeItem {


    private String chatPath;
    private String thing1_path;
    private String thing2_path;

    private String location;
    private long startDate;
    private long endDate;
    private int status;

    public FireBaseExchangeItem(
            String chatPath,
            String thing1_path,
            String thing2_path,
            String location,
            long startDate,
            long endDate,
            int status) {
        this.chatPath = chatPath;
        this.thing1_path = thing1_path;
        this.thing2_path = thing2_path;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public FireBaseExchangeItem() {
    }

    public String getChatPath() {
        return chatPath;
    }

    public String getThing1_path() {
        return thing1_path;
    }

    public String getThing2_path() {
        return thing2_path;
    }

    public String getLocation() {
        return location;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getStatus() {
        return status;
    }

    public void setChatPath(String chatPath) {
        this.chatPath = chatPath;
    }

    public void setThing1_path(String thing1_path) {
        this.thing1_path = thing1_path;
    }

    public void setThing2_path(String thing2_path) {
        this.thing2_path = thing2_path;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
