package com.zjtelcom.cpct.domain.event.DTO;

import lombok.Data;

import java.util.List;

@Data
public class EventTypeDTO{

    private Long evtTypeId;//事件类型标识，主键标识
    private String evtTypeName;//记录事件类型的名称
    private Long parEvtTypeId;//记录父级的事件类型标识
    private List<EventTypeDTO> eventTypeDTOList;//子级

}

