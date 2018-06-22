package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

/**
 * @Description EventMatchRul
 * @Author pengy
 * @Date 2018/6/22 9:31
 */
@Data
public class EventMatchRulDO extends BaseEntity {

    private Long evtMatchRulId;
    private Long eventId;
    private String evtRulName;
    private String evtRulDesc;
    private String evtRulHandleClass;
    private String evtRulExpression;


}