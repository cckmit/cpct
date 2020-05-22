package com.zjtelcom.cpct.domain.openApi.event;

import com.zjtelcom.cpct.open.base.entity.BaseEntity;
import lombok.Data;

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
