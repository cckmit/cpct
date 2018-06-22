package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.DTO.EventTypeDTO;

import java.util.List;

/**
 * @Description EventTypeService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventTypeService {

    List<EventTypeDTO> listEventTypes();

}
