package com.dadabit.everythingexchange.utils.geocode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GmapLocation {

    @SerializedName("results")
    @Expose
    private List<Result> results = null;
    @SerializedName("status")
    @Expose
    private String status;


    public String getStatus() {
        return status;
    }


    public String getAdress(){
        if (results != null){
            return this.results.get(0).getFormattedAddress();
        } else {
            return null;
        }
    }

    class Result {

        @SerializedName("formatted_address")
        @Expose
        private String formattedAddress;

        public String getFormattedAddress() {
            return formattedAddress;
        }
    }
}
