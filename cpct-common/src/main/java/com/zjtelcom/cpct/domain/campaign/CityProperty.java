/**
 * @(#)City.java, 2018/6/26.
 * <p/>
 * Copyright 2018 Netease, Inc. All rights reserved.
 * NETEASE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.zjtelcom.cpct.domain.campaign;

/**
 * Description:
 * author: linchao
 * date: 2018/06/26 10:05
 * version: V1.0
 */
public class CityProperty {

    private Long cityPropertyId;

    private String cityPropertyName;

    public Long getCityPropertyId() {
        return cityPropertyId;
    }

    public void setCityPropertyId(Long cityPropertyId) {
        this.cityPropertyId = cityPropertyId;
    }

    public String getCityPropertyName() {
        return cityPropertyName;
    }

    public void setCityPropertyName(String cityPropertyName) {
        this.cityPropertyName = cityPropertyName;
    }
}