package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class ContactChlConvertCfg extends BaseEntity {
    private Long contactChlConvertCfgId;

    private String sTransportProtocols;

    private String tTransportProtocols;

    private String transformClass;



    public Long getContactChlConvertCfgId() {
        return contactChlConvertCfgId;
    }

    public void setContactChlConvertCfgId(Long contactChlConvertCfgId) {
        this.contactChlConvertCfgId = contactChlConvertCfgId;
    }

    public String getsTransportProtocols() {
        return sTransportProtocols;
    }

    public void setsTransportProtocols(String sTransportProtocols) {
        this.sTransportProtocols = sTransportProtocols;
    }

    public String gettTransportProtocols() {
        return tTransportProtocols;
    }

    public void settTransportProtocols(String tTransportProtocols) {
        this.tTransportProtocols = tTransportProtocols;
    }

    public String getTransformClass() {
        return transformClass;
    }

    public void setTransformClass(String transformClass) {
        this.transformClass = transformClass;
    }


}