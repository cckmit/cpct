package com.zjtelcom.cpct.elastic.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CampaignInfoTree implements Serializable {
    private Long id;
    private String name;
    private boolean result;
    private String hitEntity;
    private String reason;
    private List<CampaignInfoTree> children;
    private List<Map<String,Object>> labelList;


    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<CampaignInfoTree> getChildren() {
        return children;
    }

    public void setChildren(List<CampaignInfoTree> children) {
        this.children = children;
    }

    public String getHitEntity() {
        return hitEntity;
    }

    public void setHitEntity(String hitEntity) {
        this.hitEntity = hitEntity;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public List<Map<String, Object>> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<Map<String, Object>> labelList) {
        this.labelList = labelList;
    }
}
