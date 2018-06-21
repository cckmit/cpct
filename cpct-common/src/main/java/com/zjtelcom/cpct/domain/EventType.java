package com.zjtelcom.cpct.domain;

import lombok.Data;

@Data
public class EventType {

    private Long evtTypeId;//事件类型标识，主键标识
    private String evtTypeNbr;//记录事件类型的编码
    private String evtTypeName;//记录事件类型的名称
    private Double parEvtTypeId;//记录父级的事件类型标识
    private String evtTypeDesc;//事件类型描述

}
