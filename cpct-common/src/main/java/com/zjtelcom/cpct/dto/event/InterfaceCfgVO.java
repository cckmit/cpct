package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.event.InterfaceCfg;

import java.io.Serializable;

public class InterfaceCfgVO extends InterfaceCfg implements Serializable {

    private String evtSrcName;

    public String getEvtSrcName() {
        return evtSrcName;
    }

    public void setEvtSrcName(String evtSrcName) {
        this.evtSrcName = evtSrcName;
    }
}
