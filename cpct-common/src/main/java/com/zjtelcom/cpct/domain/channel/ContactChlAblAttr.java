package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class ContactChlAblAttr extends BaseEntity {
    private Long contactChlAblAttrId;

    private Long contacChlAblId;

    private Long attrId;

    private String defaultValue;



    public Long getContactChlAblAttrId() {
        return contactChlAblAttrId;
    }

    public void setContactChlAblAttrId(Long contactChlAblAttrId) {
        this.contactChlAblAttrId = contactChlAblAttrId;
    }

    public Long getContacChlAblId() {
        return contacChlAblId;
    }

    public void setContacChlAblId(Long contacChlAblId) {
        this.contacChlAblId = contacChlAblId;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


}