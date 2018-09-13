package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.EventSceneTypeDO;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.EventSceneTypeDTO;
import com.zjtelcom.cpct.dto.event.EventTypeDTO;

import java.util.List;
import java.util.Map;

/**
 * @Description 事件场景目录service
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventSceneTypeService {

    List<EventSceneTypeDTO> listEventSceneTypes();

    void saveEventSceneType(EventSceneTypeDO eventSceneTypeDO);

    EventSceneTypeDTO getEventSceneTypeDTOById(Long evtSceneTypeId);

    Map<String, Object> updateEventSceneType(EventSceneTypeDO eventSceneTypeDO);

    Map<String, Object> delEventSceneType(Long evtSceneTypeId);
}
