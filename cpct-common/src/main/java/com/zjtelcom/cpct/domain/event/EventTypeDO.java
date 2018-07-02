package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class EventTypeDO extends BaseEntity{

    private Long evtTypeId;//事件类型标识，主键标识
    private String contactEvtTypeCode;//记录事件类型的编码
    private String contactEvtName;//记录事件类型的名称
    private Long parEvtTypeId;//记录父级的事件类型标识
    private String evtTypeDesc;//事件类型描述

}

