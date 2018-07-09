package com.zjtelcom.cpct.domain;

public class SysArea {
    private Integer areaId;

    private Integer parentArea;

    private String name;

    private Integer areaLevel;

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
}