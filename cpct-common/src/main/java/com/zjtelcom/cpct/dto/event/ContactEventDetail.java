package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.filter.FilterRule;

import java.util.List;

/**
 * @Description 事件详细信息
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
public class ContactEventDetail extends ContactEvt{

    private List<EventItem> contactEvtItems;
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

    public List<EventItem> getContactEvtItems() {
        return contactEvtItems;
    }

    public void setContactEvtItems(List<EventItem> contactEvtItems) {
        this.contactEvtItems = contactEvtItems;
    }

    public List<ContactEvtMatchRul> getContactEvtMatchRuls() {
        return contactEvtMatchRuls;
    }

    public void setContactEvtMatchRuls(List<ContactEvtMatchRul> contactEvtMatchRuls) {
        this.contactEvtMatchRuls = contactEvtMatchRuls;
    }

    public List<ContactEvtTrigRul> getContactEvtTrigRuls() {
        return contactEvtTrigRuls;
    }

    public void setContactEvtTrigRuls(List<ContactEvtTrigRul> contactEvtTrigRuls) {
        this.contactEvtTrigRuls = contactEvtTrigRuls;
    }

    public List<EvtSceneCamRel> getEvtSceneCamRels() {
        return evtSceneCamRels;
    }

    public void setEvtSceneCamRels(List<EvtSceneCamRel> evtSceneCamRels) {
        this.evtSceneCamRels = evtSceneCamRels;
    }

    public InterfaceCfgDetail getInterfaceCfgDetail() {
        return interfaceCfgDetail;
    }

    public void setInterfaceCfgDetail(InterfaceCfgDetail interfaceCfgDetail) {
        this.interfaceCfgDetail = interfaceCfgDetail;
    }

    public List<FilterRule> getFilterRules() {
        return filterRules;
    }

    public void setFilterRules(List<FilterRule> filterRules) {
        this.filterRules = filterRules;
    }

    public List<MktCamEvtRel> getMktCamEvtRels() {
        return mktCamEvtRels;
    }

    public void setMktCamEvtRels(List<MktCamEvtRel> mktCamEvtRels) {
        this.mktCamEvtRels = mktCamEvtRels;
    }

    public String getMktCampaignTypeName() {
        return mktCampaignTypeName;
    }

    public void setMktCampaignTypeName(String mktCampaignTypeName) {
        this.mktCampaignTypeName = mktCampaignTypeName;
    }

    public String getEventTypeName() {
        return eventTypeName;
    }

    public void setEventTypeName(String eventTypeName) {
        this.eventTypeName = eventTypeName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public EventMatchRulDetail getEventMatchRulDetail() {
        return eventMatchRulDetail;
    }

    public void setEventMatchRulDetail(EventMatchRulDetail eventMatchRulDetail) {
        this.eventMatchRulDetail = eventMatchRulDetail;
    }
}
