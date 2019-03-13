package com.zjtelcom.cpct.domain.channel;

import com.zjtelcom.cpct.BaseEntity;

import java.util.Date;

public class EventRel extends BaseEntity {
    private Long complexEvtRelaId;

    private Long aEvtId;

    private Long zEvtId;

    private Long sort;



    public Long getComplexEvtRelaId() {
        return complexEvtRelaId;
    }

    public void setComplexEvtRelaId(Long complexEvtRelaId) {
        this.complexEvtRelaId = complexEvtRelaId;
    }

    public Long getaEvtId() {
        return aEvtId;
    }

    public void setaEvtId(Long aEvtId) {
        this.aEvtId = aEvtId;
    }

    public Long getzEvtId() {
        return zEvtId;
    }

    public void setzEvtId(Long zEvtId) {
        this.zEvtId = zEvtId;
    }

    public Long getSort() {
        return sort;
    }

    public void setSort(Long sort) {
        this.sort = sort;
    }


}