package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;
import java.util.List;

/**
 * @Description 事件详细信息
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class ContactEvtDetail extends ContactEvt{

    private List<ContactEvtItem> contactEvtItems;
    private List<ContactEvtMatchRul> contactEvtMatchRuls;
    private List<ContactEvtTrigRul> contactEvtTrigRuls;
    private InterfaceCfgDetail interfaceCfgDetail;

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
}
