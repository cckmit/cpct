package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktObjKeywordsRel extends BaseEntity {
    private Long relId;

    private String keywordId;

    private String keywordType;

    private String keywordObj;

    private String keywordCol;

    private String keywordValue;

    private Long regionId;

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

    public String getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(String keywordId) {
        this.keywordId = keywordId;
    }

    public String getKeywordType() {
        return keywordType;
    }

    public void setKeywordType(String keywordType) {
        this.keywordType = keywordType;
    }

    public String getKeywordObj() {
        return keywordObj;
    }

    public void setKeywordObj(String keywordObj) {
        this.keywordObj = keywordObj;
    }

    public String getKeywordCol() {
        return keywordCol;
    }

    public void setKeywordCol(String keywordCol) {
        this.keywordCol = keywordCol;
    }

    public String getKeywordValue() {
        return keywordValue;
    }

    public void setKeywordValue(String keywordValue) {
        this.keywordValue = keywordValue;
    }



    public Long getRegionId() {
        return regionId;
    }

    public void setRegionId(Long regionId) {
        this.regionId = regionId;
    }
}