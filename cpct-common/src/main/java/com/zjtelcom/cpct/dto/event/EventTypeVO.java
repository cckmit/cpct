package com.zjtelcom.cpct.dto.event;

import java.io.Serializable;

public class EventTypeVO extends ContactEvtType implements Serializable {
    private String parentName;

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }
}
