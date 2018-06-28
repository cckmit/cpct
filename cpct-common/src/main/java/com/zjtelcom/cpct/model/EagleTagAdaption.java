package com.zjtelcom.cpct.model;

public class EagleTagAdaption {
    private Integer adapId;

    private String sourceTableColumnName;

    private String fitDomain;

    private String script;
    
    private String adapClassify;

    public Integer getAdapId() {
        return adapId;
    }

    public void setAdapId(Integer adapId) {
        this.adapId = adapId;
    }

    public String getSourceTableColumnName() {
        return sourceTableColumnName;
    }

    public void setSourceTableColumnName(String sourceTableColumnName) {
        this.sourceTableColumnName = sourceTableColumnName;
    }

    public String getFitDomain() {
        return fitDomain;
    }

    public void setFitDomain(String fitDomain) {
        this.fitDomain = fitDomain;
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getAdapClassify() {
        return adapClassify;
    }

    public void setAdapClassify(String adapClassify) {
        this.adapClassify = adapClassify;
    }
}