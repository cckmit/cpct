/**
 * @(#)City.java, 2018/6/26.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.domain.campaign;

import java.io.Serializable;
import java.util.List;

/**
 * Description:
 * author: linchao
 * date: 2018/06/26 10:05
 * version: V1.0
 */
public class City  implements Serializable {

    /**
     * 城市Id
     */
    private Long mktStrategyConfRegionRelId;

    /**
     * 地市
     */
    private CityProperty applyCity;

    /**
     * 区县集合
     */
    private List<CityProperty> applyCountys;

    /**
     * 支局集合
     */
    private List<CityProperty> applyBranchs;

    /**
     * 网格集合
     */
    private List<CityProperty> applyGriddings;

    public Long getMktStrategyConfRegionRelId() {
        return mktStrategyConfRegionRelId;
    }

    public void setMktStrategyConfRegionRelId(Long mktStrategyConfRegionRelId) {
        this.mktStrategyConfRegionRelId = mktStrategyConfRegionRelId;
    }

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