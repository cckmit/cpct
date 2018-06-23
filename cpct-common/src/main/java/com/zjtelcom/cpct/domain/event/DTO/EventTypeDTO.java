package com.zjtelcom.cpct.domain.event.DTO;

import com.zjtelcom.cpct.BaseEntity;
import lombok.Data;

import java.util.List;

@Data
public class EventTypeDTO extends BaseEntity{

    private Long evtTypeId;//事件类型标识，主键标识
    private String evtTypeNbr;//记录事件类型的编码
    private String evtTypeName;//记录事件类型的名称
    private Long parEvtTypeId;//记录父级的事件类型标识
    private String evtTypeDesc;//事件类型描述
    private List<EventTypeDTO> eventTypeDTOList;//子级

}

