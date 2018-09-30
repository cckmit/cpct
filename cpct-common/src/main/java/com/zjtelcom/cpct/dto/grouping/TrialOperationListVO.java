package com.zjtelcom.cpct.dto.grouping;

import org.apache.commons.collections4.map.AbstractMapDecorator;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TrialOperationListVO  implements Serializable {

    private List<SimpleInfo> titleList;

    private List<Map<String,Object>> hitsList;

    private Long total;


    //todo 分区域人数


    public List<SimpleInfo> getTitleList() {
        return titleList;
    }

    public void setTitleList(List<SimpleInfo> titleList) {
        this.titleList = titleList;
    }

    public List<Map<String, Object>> getHitsList() {
        return hitsList;
    }

    public void setHitsList(List<Map<String, Object>> hitsList) {
        this.hitsList = hitsList;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
}
