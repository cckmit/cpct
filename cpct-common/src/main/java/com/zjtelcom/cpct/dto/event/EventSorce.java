package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.domain.event.EventSorceDO;

public class EventSorce extends EventSorceDO {

    private String createStaffName; //创建人名称
    private String updateStaffName; //更新人名称
    private String regionName;//区域名称

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

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
