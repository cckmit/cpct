package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.event.InterfaceCfg;

import java.io.Serializable;

public class InterfaceCfgVO extends InterfaceCfg implements Serializable {

    private String evtSrcName;
    private String callerName;
    private String providerName;

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getEvtSrcName() {
        return evtSrcName;
    }

    public void setEvtSrcName(String evtSrcName) {
        this.evtSrcName = evtSrcName;
    }
}
