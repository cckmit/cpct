package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class MktKeywords extends BaseEntity {
    private Long keywordId;

    private String keywrod;

    private String keywordDesc;



    public Long getKeywordId() {
        return keywordId;
    }

    public void setKeywordId(Long keywordId) {
        this.keywordId = keywordId;
    }

    public String getKeywrod() {
        return keywrod;
    }

    public void setKeywrod(String keywrod) {
        this.keywrod = keywrod;
    }

    public String getKeywordDesc() {
        return keywordDesc;
    }

    public void setKeywordDesc(String keywordDesc) {
        this.keywordDesc = keywordDesc;
    }


}