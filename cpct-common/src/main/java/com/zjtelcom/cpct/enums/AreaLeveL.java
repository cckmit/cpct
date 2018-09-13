/**
 * @(#)ConfAttrEnum.java, 2018/7/3.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.enums;

/**
 * Description:
 * author: linchao
 * date: 2018/07/03 10:21
 * version: V1.0
 */
public enum AreaLeveL {

    PROVINCE(0, "省级"), CITY(1, "地市"), COUNTYS(2, "区县"), BRANCHS(3, "支局"), GRIDDINGS(4, "网格");
    private Integer areaLevel;
    private String areaName;

    AreaLeveL() {
    }

    AreaLeveL(Integer areaLevel, String areaName) {
        this.areaLevel = areaLevel;
        this.areaName = areaName;
    }

    public Integer getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(Integer areaLevel) {
        this.areaLevel = areaLevel;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
}