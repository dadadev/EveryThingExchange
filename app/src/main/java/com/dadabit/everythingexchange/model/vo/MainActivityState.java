package com.dadabit.everythingexchange.model.vo;


import java.util.ArrayList;
import java.util.List;

public class MainActivityState {


    private List<Integer> adapterTypeStack;

    private int chosenCategory;
    private int chosenMyThing;
    private int chosenChat;
    private int chosenFireBaseThing;

    private int firstVisiblePosition;
    private int bottomNavigation;
    private int appBarLayout;

    private String chosenLocation;

    private boolean isNewThingAdd;


    public MainActivityState() {
        adapterTypeStack = new ArrayList<>();
        isNewThingAdd = false;
    }

    public int getAdapterType() {
        return adapterTypeStack.size() > 0 ? adapterTypeStack.get(adapterTypeStack.size()-1) : 0;
    }

    public int getPreviousAdapterType() {
        if (adapterTypeStack.size() > 0){

            adapterTypeStack.remove(adapterTypeStack.size()-1);

            return adapterTypeStack.size() > 1 ? adapterTypeStack.get(adapterTypeStack.size()-1) : 0;

        } else {
            return 0;
        }
    }


    public int getChosenCategory() {
        return chosenCategory;
    }

    public int getChosenMyThing() {
        return chosenMyThing;
    }

    public int getChosenChat() {
        return chosenChat;
    }

    public int getChosenFireBaseThing() {
        return chosenFireBaseThing;
    }

    public String getChosenLocation() {
        return chosenLocation;
    }

    public int getFirstVisiblePosition() {
        return firstVisiblePosition;
    }

    public int getBottomNavigation() {
        return bottomNavigation;
    }

    public int getAppBarLayout() {
        return appBarLayout;
    }


    public boolean isNewThingAdd() {
        return isNewThingAdd;
    }




    public void setAdapterType(int adapterType) {
        if (getAdapterType() != adapterType){
            adapterTypeStack.add(adapterType);
        }
    }

    public void setChosenCategory(int chosenCategory) {
        this.chosenCategory = chosenCategory;
    }

    public void setChosenMyThing(int chosenMyThing) {
        this.chosenMyThing = chosenMyThing;
    }

    public void setChosenChatId(int chosenChat) {
        this.chosenChat = chosenChat;
    }

    public void setChosenFireBaseThing(int chosenFireBaseThing) {
        this.chosenFireBaseThing = chosenFireBaseThing;
    }

    public void setChosenLocation(String chosenLocation) {
        this.chosenLocation = chosenLocation;
    }

    public void setFirstVisiblePosition(int firstVisiblePosition) {
        this.firstVisiblePosition = firstVisiblePosition;
    }

    public void setBottomNavigation(int bottomNavigation) {
        this.bottomNavigation = bottomNavigation;
    }

    public void setAppBarLayout(int appBarLayout) {
        this.appBarLayout = appBarLayout;
    }


    public void setNewThingAdd(boolean newThingAdd) {
        isNewThingAdd = newThingAdd;
    }


}
