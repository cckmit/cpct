package com.zjtelcom.cpct.dto.channel;

import java.io.Serializable;
import java.util.List;

public class ProductParam implements Serializable {
    private List<Long> idList;

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }
}