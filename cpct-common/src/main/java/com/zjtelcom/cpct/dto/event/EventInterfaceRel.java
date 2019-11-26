package com.zjtelcom.cpct.dto.event;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

@Data
public class EventInterfaceRel extends BaseEntity  {

    private Long evtInterfaceRelId;
    private Long evtId; //事件主键标识
    private Long interfaceId;//接口配置标识，主键标识
    private String channelCode; //关联渠道编码
}
