package com.zjtelcom.cpct.model;

public class EagleTag {
    private Long tagRowId;

    private Long ctasTableDefinitionRowId;

    private String tagName;

    private String sourceTableColumnName;

    private String sourceTableColumnType;

    private String sensitiveColumnFlag;

    private String sensitiveType;

    private String usedStatus;

    private String fitDomain;

    public Long getTagRowId() {
        return tagRowId;
    }

    public void setTagRowId(Long tagRowId) {
        this.tagRowId = tagRowId;
    }

    public Long getCtasTableDefinitionRowId() {
        return ctasTableDefinitionRowId;
    }

    public void setCtasTableDefinitionRowId(Long ctasTableDefinitionRowId) {
        this.ctasTableDefinitionRowId = ctasTableDefinitionRowId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public String getSourceTableColumnName() {
        return sourceTableColumnName;
    }

    public void setSourceTableColumnName(String sourceTableColumnName) {
        this.sourceTableColumnName = sourceTableColumnName;
    }

    public String getSourceTableColumnType() {
        return sourceTableColumnType;
    }

    public void setSourceTableColumnType(String sourceTableColumnType) {
        this.sourceTableColumnType = sourceTableColumnType;
    }

    public String getSensitiveColumnFlag() {
        return sensitiveColumnFlag;
    }

    public void setSensitiveColumnFlag(String sensitiveColumnFlag) {
        this.sensitiveColumnFlag = sensitiveColumnFlag;
    }

    public String getSensitiveType() {
        return sensitiveType;
    }

    public void setSensitiveType(String sensitiveType) {
        this.sensitiveType = sensitiveType;
    }

    public String getUsedStatus() {
        return usedStatus;
    }

    public void setUsedStatus(String usedStatus) {
        this.usedStatus = usedStatus;
    }

    public String getFitDomain() {
        return fitDomain;
    }

    public void setFitDomain(String fitDomain) {
        this.fitDomain = fitDomain;
    }
}