package com.zjtelcom.cpct.dto.strategy;

import com.zjtelcom.cpct.domain.campaign.City;

import java.util.Date;
import java.util.List;

/**
 * 策略配置详情
 */
public class MktStrategyConfDetail extends MktStrategyConf {

    private Long mktCampaignId;
    /**
     * 下发城市列表
     */
/*    private List<City> cityList;*/

    /**
     * 策略配置下规则列表
     */
    private List<MktStrategyConfRule> mktStrategyConfRuleList;

/*    public List<City> getCityList() {
        return cityList;
    }

    public void setCityList(List<City> cityList) {
        this.cityList = cityList;
    }*/


    public List<MktStrategyConfRule> getMktStrategyConfRuleList() {
        return mktStrategyConfRuleList;
    }

    public void setMktStrategyConfRuleList(List<MktStrategyConfRule> mktStrategyConfRuleList) {
        this.mktStrategyConfRuleList = mktStrategyConfRuleList;
    }

    public Long getMktCampaignId() {
        return mktCampaignId;
    }

    public void setMktCampaignId(Long mktCampaignId) {
        this.mktCampaignId = mktCampaignId;
    }
}