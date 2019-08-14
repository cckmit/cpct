package com.zjtelcom.cpct.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class CampaignInfoTree implements Serializable {
    private Long id;
    private String name;
    private String result;
    private String hitEntity;
    private String reason;
    private List<CampaignInfoTree> children;
    private List<Map<String,Object>> labelList;
    private String type;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

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

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public List<Map<String, Object>> getLabelList() {
        return labelList;
    }

    public void setLabelList(List<Map<String, Object>> labelList) {
        this.labelList = labelList;
    }
}
