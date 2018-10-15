package com.zjtelcom.cpct.dto.campaign;

import com.zjtelcom.cpct.BaseEntity;

public class MktCamChlConfAttr extends BaseEntity {
    private Long contactChlAttrRstrId;

    private Long evtContactConfId;

    private Long attrId;

    private Long attrValueId;

    private String attrValue;

    private String attrValueName;

    public Long getContactChlAttrRstrId() {
        return contactChlAttrRstrId;
    }

    public void setContactChlAttrRstrId(Long contactChlAttrRstrId) {
        this.contactChlAttrRstrId = contactChlAttrRstrId;
    }

    public Long getEvtContactConfId() {
        return evtContactConfId;
    }

    public void setEvtContactConfId(Long evtContactConfId) {
        this.evtContactConfId = evtContactConfId;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public Long getAttrValueId() {
        return attrValueId;
    }

    public void setAttrValueId(Long attrValueId) {
        this.attrValueId = attrValueId;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getAttrValueName() {
        return attrValueName;
    }

    public void setAttrValueName(String attrValueName) {
        this.attrValueName = attrValueName;
    }
}