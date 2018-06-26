package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.event.EventMatchRulDO;
import lombok.Data;

import java.util.List;

/**
 * @Description ScenceCampaignDTO
 * @Author pengy
 * @Date 2018/6/23 11:49
 */
@Data
public class ScenceCampaignDTO {

    private List<MktCampaignDO> mktCampaignDTOList;//活动实体类
    private List<EventMatchRulDO> eventMatchRulDOList;//事件匹配规则实体类

}
