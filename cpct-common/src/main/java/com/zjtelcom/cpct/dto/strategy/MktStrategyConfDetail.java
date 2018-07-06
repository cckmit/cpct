package com.zjtelcom.cpct.dto.strategy;

import com.zjtelcom.cpct.domain.campaign.City;

import java.util.Date;
import java.util.List;

public class MktStrategyConfDetail extends MktStrategyConf {

    /**
     * 下发城市列表
     */
    private List<City> cityList;

    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }

}