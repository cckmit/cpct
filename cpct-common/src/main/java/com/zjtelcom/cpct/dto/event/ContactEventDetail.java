package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import lombok.Data;

import java.util.List;

/**
 * @Description 事件详细信息
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class ContactEventDetail extends ContactEvt{

    private List<ContactEvtItem> contactEvtItems;
    private List<ContactEvtMatchRul> contactEvtMatchRuls;
    private List<ContactEvtTrigRul> contactEvtTrigRuls;
    private List<EvtSceneCamRel> evtSceneCamRels;
    private InterfaceCfgDetail interfaceCfgDetail;
    private List<FilterRule> filterRules;
    private List<MktCamEvtRel> mktCamEvtRels;
    private String mktCampaignTypeName;
    private String eventTypeName;
    private String interfaceName;
    private EventMatchRulDetail eventMatchRulDetail;

}
