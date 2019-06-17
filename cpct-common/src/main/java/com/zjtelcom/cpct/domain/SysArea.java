package com.zjtelcom.cpct.domain;

import java.io.Serializable;
import java.util.List;

public class SysArea implements Serializable {
    private Integer areaId;

    private Integer parentArea;

    private String name;

    private Integer areaLevel;

    private String orgArea;

    /**
     * 下级区域列表
     */
    private List<SysArea> childAreaList;


    public String getOrgArea() {
        return orgArea;
    }

    public void setOrgArea(String orgArea) {
        this.orgArea = orgArea;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getParentArea() {
        return parentArea;
    }

    public void setParentArea(Integer parentArea) {
        this.parentArea = parentArea;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(Integer areaLevel) {
        this.areaLevel = areaLevel;
    }

    public List<SysArea> getChildAreaList() {
        return childAreaList;
    }

    public void setChildAreaList(List<SysArea> childAreaList) {
        this.childAreaList = childAreaList;
    }
}