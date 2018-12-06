package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;

import java.util.List;

/**
 * @Author:sunpeng
 * @Descirption:营销活动服务对接dto
 * @Date: 2018/6/26.
 */
public class MktCampaignDetail extends MktCampaign{


    /**
     * 策略配置信息
     */
    List<MktStrategyConfDetail> mktStrategyConfDetailList;



    public List<MktStrategyConfDetail> getMktStrategyConfDetailList() {
        return mktStrategyConfDetailList;
    }

    public void setMktStrategyConfDetailList(List<MktStrategyConfDetail> mktStrategyConfDetailList) {
        this.mktStrategyConfDetailList = mktStrategyConfDetailList;
    }


}