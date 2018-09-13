package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.event.EventSorceDO;

public class EventSorce extends EventSorceDO {

    private String createStaffName; //创建人名称
    private String updateStaffName; //更新人名称

    public String getCreateStaffName() {
        return createStaffName;
    }

    public void setCreateStaffName(String createStaffName) {
        this.createStaffName = createStaffName;
    }

    public String getUpdateStaffName() {
        return updateStaffName;
    }

    public void setUpdateStaffName(String updateStaffName) {
        this.updateStaffName = updateStaffName;
    }
}
