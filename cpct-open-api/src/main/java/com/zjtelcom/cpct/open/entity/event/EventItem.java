package com.zjtelcom.cpct.open.entity.event;

import lombok.Data;

@Data
public class EventItem {
    private Long evtItemId;
    private String evtItemName;
    private String evtItemCode;
    private String valueDataType;
    private String evtItemFormat;
    private String isNullable;
    private Integer evtItemLength;
    private Integer standardSort;
    private String remark;
}
