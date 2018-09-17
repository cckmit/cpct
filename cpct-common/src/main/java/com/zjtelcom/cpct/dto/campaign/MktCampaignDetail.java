package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.domain.SysArea;
import com.zjtelcom.cpct.domain.campaign.MktCamGrpRul;
import com.zjtelcom.cpct.dto.channel.ChannelDetail;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dto.strategy.MktStrategyConfDetail;
import com.zjtelcom.cpct.dto.strategy.MktStrategyDetail;
import lombok.Data;

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