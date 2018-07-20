package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.Map;

public class QryMktScriptReq implements Serializable {
    private String labelName;
    private String labelCode;
    private Integer scope;
    private String conditionType;
    private String fitDomain;
    private Map<String,Object> params;
    private Integer page;
    private Integer pageSize;


    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelCode() {
        return labelCode;
    }

    public void setLabelCode(String labelCode) {
        this.labelCode = labelCode;
    }

    public Integer getScope() {
        return scope;
    }

    public void setScope(Integer scope) {
        this.scope = scope;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getFitDomain() {
        return fitDomain;
    }

    public void setFitDomain(String fitDomain) {
        this.fitDomain = fitDomain;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
