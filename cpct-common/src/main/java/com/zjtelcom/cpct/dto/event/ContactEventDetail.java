package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.campaign.MktCampaign;
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

    public List<ContactEvtItem> getContactEvtItems() {
        return contactEvtItems;
    }

    public void setContactEvtItems(List<ContactEvtItem> contactEvtItems) {
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
}
