package com.zjtelcom.cpct.open.entity.event;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

import java.util.Date;

@Data
public class EventType extends BaseEntity {

    Long evtTypeId;
    String evtTypeNbr;
    String evtTypeName;
    String evtTypeDesc;
    Long parEvtTypeId;
    String statusCd;
    String statusDate;
    String remark;

}
