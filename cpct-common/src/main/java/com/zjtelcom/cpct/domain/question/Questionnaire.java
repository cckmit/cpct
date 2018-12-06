package com.zjtelcom.cpct.domain.question;

import com.zjtelcom.cpct.BaseEntity;

public class Questionnaire extends BaseEntity{
    private Long naireId;

    private String naireName;

    private String naireType;//1000	营销问卷;2000	维挽问卷

    private String markType;

    private Integer nairePoints;

    private String naireDesc;

    private String startTip;

    private String endTip;


    public Long getNaireId() {
        return naireId;
    }

    public void setNaireId(Long naireId) {
        this.naireId = naireId;
    }

    public String getNaireName() {
        return naireName;
    }

    public void setNaireName(String naireName) {
        this.naireName = naireName;
    }

    public String getNaireType() {
        return naireType;
    }

    public void setNaireType(String naireType) {
        this.naireType = naireType;
    }

    public String getMarkType() {
        return markType;
    }

    public void setMarkType(String markType) {
        this.markType = markType;
    }

    public Integer getNairePoints() {
        return nairePoints;
    }

    public void setNairePoints(Integer nairePoints) {
        this.nairePoints = nairePoints;
    }

    public String getNaireDesc() {
        return naireDesc;
    }

    public void setNaireDesc(String naireDesc) {
        this.naireDesc = naireDesc;
    }

    public String getStartTip() {
        return startTip;
    }

    public void setStartTip(String startTip) {
        this.startTip = startTip;
    }

    public String getEndTip() {
        return endTip;
    }

    public void setEndTip(String endTip) {
        this.endTip = endTip;
    }

}