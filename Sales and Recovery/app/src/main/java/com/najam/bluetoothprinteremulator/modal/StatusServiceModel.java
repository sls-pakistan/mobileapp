package com.najam.bluetoothprinteremulator.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StatusServiceModel {
    @SerializedName("feed_date")
    private String feedDate;
    @Expose
    private String message;
    @Expose
    private int status;

    public String getFeedDate() {
        return feedDate;
    }

    public void setFeedDate(String feedDate) {
        this.feedDate = feedDate;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
