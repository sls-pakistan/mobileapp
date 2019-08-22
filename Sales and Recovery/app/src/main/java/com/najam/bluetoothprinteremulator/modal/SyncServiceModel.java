package com.najam.bluetoothprinteremulator.modal;

import com.google.gson.annotations.Expose;

public class SyncServiceModel {
    @Expose
    private Feed feed;
    @Expose
    private int status;
    @Expose
    private String message;

    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
