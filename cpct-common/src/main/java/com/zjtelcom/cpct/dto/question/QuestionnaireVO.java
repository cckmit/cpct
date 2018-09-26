package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;

public class QuestionnaireVO implements Serializable {
    private Long naireId;

    private String naireName;

    private String naireType;//1000	营销问卷;2000	维挽问卷

    private String naireTypeName;

    private String markType;

    private Integer nairePoints;

    private String naireDesc;

    private String status;

    private String statusSt;


    public String getStatusSt() {
        return statusSt;
    }

    public void setStatusSt(String statusSt) {
        this.statusSt = statusSt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNaireTypeName() {
        return naireTypeName;
    }

    public void setNaireTypeName(String naireTypeName) {
        this.naireTypeName = naireTypeName;
    }

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
}
