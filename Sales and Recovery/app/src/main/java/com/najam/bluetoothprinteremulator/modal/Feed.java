
package com.najam.bluetoothprinteremulator.modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Feed {
    @SerializedName("agent_id")
    private String agentId;
    @Expose
    private String id;
    @SerializedName("incremented_feed")
    private Object incrementedFeed;
    @SerializedName("incremented_feed_date")
    private Object incrementedFeedDate;
    @SerializedName("input_feed")
    private String inputFeed;
    @SerializedName("input_feed_date")
    private String inputFeedDate;
    @SerializedName("is_processed")
    private String isProcessed;
    @SerializedName("process_datetime")
    private Object processDatetime;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getIncrementedFeed() {
        return incrementedFeed;
    }

    public void setIncrementedFeed(Object incrementedFeed) {
        this.incrementedFeed = incrementedFeed;
    }

    public Object getIncrementedFeedDate() {
        return incrementedFeedDate;
    }

    public void setIncrementedFeedDate(Object incrementedFeedDate) {
        this.incrementedFeedDate = incrementedFeedDate;
    }

    public String getInputFeed() {
        return inputFeed;
    }

    public void setInputFeed(String inputFeed) {
        this.inputFeed = inputFeed;
    }

    public String getInputFeedDate() {
        return inputFeedDate;
    }

    public void setInputFeedDate(String inputFeedDate) {
        this.inputFeedDate = inputFeedDate;
    }

    public String getIsProcessed() {
        return isProcessed;
    }

    public void setIsProcessed(String isProcessed) {
        this.isProcessed = isProcessed;
    }

    public Object getProcessDatetime() {
        return processDatetime;
    }

    public void setProcessDatetime(Object processDatetime) {
        this.processDatetime = processDatetime;
    }

}
