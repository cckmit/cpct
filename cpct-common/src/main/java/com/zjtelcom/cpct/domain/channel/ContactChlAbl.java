package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class ContactChlAbl extends BaseEntity {
    private Long contacChlAblId;

    private Long contactChlConvertCfgId;

    private Long contactChlId;

    private String pushType;


    public Long getContacChlAblId() {
        return contacChlAblId;
    }

    public void setContacChlAblId(Long contacChlAblId) {
        this.contacChlAblId = contacChlAblId;
    }

    public Long getContactChlConvertCfgId() {
        return contactChlConvertCfgId;
    }

    public void setContactChlConvertCfgId(Long contactChlConvertCfgId) {
        this.contactChlConvertCfgId = contactChlConvertCfgId;
    }

    public Long getContactChlId() {
        return contactChlId;
    }

    public void setContactChlId(Long contactChlId) {
        this.contactChlId = contactChlId;
    }

    public String getPushType() {
        return pushType;
    }

    public void setPushType(String pushType) {
        this.pushType = pushType;
    }


}