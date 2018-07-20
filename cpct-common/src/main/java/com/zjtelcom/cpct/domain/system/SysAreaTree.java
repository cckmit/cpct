/**
 * @(#)SysAreaTree.java, 2018/7/19.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.domain.system;

import java.util.List;

/**
 * Description:
 * author: linchao
 * date: 2018/07/19 19:13
 * version: V1.0
 */
public class SysAreaTree {
    /**
     * 区域标识
     */
    private Integer areaId;
    /**
     * 父节点标识
     */
    private Integer parentArea;
    /**
     * 区域等级
     */
    private Integer areaLevel;
    /**
     * 下级区域列表
     */
    private List<SysAreaTree> childrenAreaList;

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

    public Integer getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(Integer areaLevel) {
        this.areaLevel = areaLevel;
    }

    public List<SysAreaTree> getChildrenAreaList() {
        return childrenAreaList;
    }

    public void setChildrenAreaList(List<SysAreaTree> childrenAreaList) {
        this.childrenAreaList = childrenAreaList;
    }
}