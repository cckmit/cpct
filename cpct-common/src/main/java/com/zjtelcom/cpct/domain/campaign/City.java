/**
 * @(#)City.java, 2018/6/26.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.domain.campaign;

import java.util.List;

/**
 * Description:
 * author: linchao
 * date: 2018/06/26 10:05
 * version: V1.0
 */
public class City {

    private CityProperty applyCity;

    private List<CityProperty> applyCountys;

    private List<CityProperty> applyBranchs;

    private List<CityProperty> applyGriddings;

    public CityProperty getApplyCity() {
        return applyCity;
    }

    public void setApplyCity(CityProperty applyCity) {
        this.applyCity = applyCity;
    }

    public List<CityProperty> getApplyCountys() {
        return applyCountys;
    }

    public void setApplyCountys(List<CityProperty> applyCountys) {
        this.applyCountys = applyCountys;
    }

    public List<CityProperty> getApplyBranchs() {
        return applyBranchs;
    }

    public void setApplyBranchs(List<CityProperty> applyBranchs) {
        this.applyBranchs = applyBranchs;
    }

    public List<CityProperty> getApplyGriddings() {
        return applyGriddings;
    }

    public void setApplyGriddings(List<CityProperty> applyGriddings) {
        this.applyGriddings = applyGriddings;
    }
}