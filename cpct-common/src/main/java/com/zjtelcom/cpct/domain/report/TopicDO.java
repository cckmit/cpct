package com.zjtelcom.cpct.domain.report;

import com.zjtelcom.cpct.BaseEntity;

import java.io.Serializable;
import java.util.Date;



public class TopicDO extends BaseEntity {
    private int topicId;   //主题id
    private String year;   //年份
    private String season;   //季度
    private String topicName;  //主题名称
    private String topicCode;   //主题编码
    private String description;

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }

    public String getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(String topicCode) {
        this.topicCode = topicCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
