package com.zjtelcom.cpct.domain.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class EventSorce extends BaseEntity {

    private Long evtSrcId;//事件源标识
    private String evtSrcCode;//事件源编码
    private String evtSrcName;//事件源名称
    private String evtSrcDesc;//事件源描述
    private Long regionId;//记录适用区域标识，指定公共管理区域

}
