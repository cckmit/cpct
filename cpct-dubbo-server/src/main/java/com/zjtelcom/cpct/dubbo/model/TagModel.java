package com.zjtelcom.cpct.dubbo.model;

public class TagModel {
    private Long tagRowId;//标签表主键
    private Long ctasTableDefinitionRowId;//源表定义主键
    private String tagName;//标签名称
    private String sourceTableColumnName;//源表字段
    private String sourceTableColumnType;//标签字段类型
    private String usedStatus;//
    private String sensitiveColumnFlag;//敏感字段标志 1：敏感字段，2：不敏感字段
    private String sensitiveType;//脱敏类型 1：全部打*（适用地址等字段），2、首字符除外打*（适用姓名等字段）;3、中间5位打*（适用手机等字段）;4、中间9位打*（适用身份证号等字段）;
    private String fitDomain;//适用域
    private String showFlag;//


    public String getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(String showFlag) {
        this.showFlag = showFlag;
    }

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

    public String getUsedStatus() {
        return usedStatus;
    }

    public void setUsedStatus(String usedStatus) {
        this.usedStatus = usedStatus;
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

    public String getFitDomain() {
        return fitDomain;
    }

    public void setFitDomain(String fitDomain) {
        this.fitDomain = fitDomain;
    }
}
