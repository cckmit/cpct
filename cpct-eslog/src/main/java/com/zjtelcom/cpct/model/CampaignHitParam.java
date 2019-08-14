package com.zjtelcom.cpct.model;

import java.io.Serializable;
import java.util.Date;

public class CampaignHitParam implements Serializable {
    private String isi;//
    private String eventCode;
    private Date startTime;
    private Date endTime;
    private String assetNumber;
    private String searchBy;
    private int from;


    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getAssetNumber() {
        return assetNumber;
    }

    public void setAssetNumber(String assetNumber) {
        this.assetNumber = assetNumber;
    }

    public String getSearchBy() {
        return searchBy;
    }

    public void setSearchBy(String searchBy) {
        this.searchBy = searchBy;
    }
}
