package com.zjtelcom.cpct.open.entity.event;

import lombok.Data;

import java.util.Date;

@Data
public class EventMatchRul {

    private Long evtMatchRulId;
    private Long eventId;
    private String evtRulName;
    private String evtRulDesc;
    private String evtRulHandleClass;
    private String evtRulExpression;
    private String statusCd;
    private String statusDate;
    private String remark;

}
