package com.zjtelcom.cpct.dto.question;

import java.io.Serializable;
import java.util.List;

public class QuestionnaireParam implements Serializable {

    private Long naireId;

    private String naireName;

    private String naireType;//1000	营销问卷;2000	维挽问卷

    private String markType;

    private Integer nairePoints;

    private String naireDesc;

    private List<Long> questionIdList;

    private Integer page;

    private Integer pageSize;



    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
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

    public List<Long> getQuestionIdList() {
        return questionIdList;
    }

    public void setQuestionIdList(List<Long> questionIdList) {
        this.questionIdList = questionIdList;
    }
}
