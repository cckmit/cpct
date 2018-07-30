package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;

public class ItemEditVO implements Serializable {
    private Long evtItemId;//事件采集项主键
    private String evtItemName;//记录事件采集项的名称
    private String evtItemCode;//记录事件采集项的编码，主要用于格式化
    private String evtItemDesc;//采集项描述

    public Long getEvtItemId() {
        return evtItemId;
    }

    public void setEvtItemId(Long evtItemId) {
        this.evtItemId = evtItemId;
    }

    public String getEvtItemName() {
        return evtItemName;
    }

    public void setEvtItemName(String evtItemName) {
        this.evtItemName = evtItemName;
    }

    public String getEvtItemCode() {
        return evtItemCode;
    }

    public void setEvtItemCode(String evtItemCode) {
        this.evtItemCode = evtItemCode;
    }

    public String getEvtItemDesc() {
        return evtItemDesc;
    }

    public void setEvtItemDesc(String evtItemDesc) {
        this.evtItemDesc = evtItemDesc;
    }
}
