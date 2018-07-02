package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.EventSceneTypeDO;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.EventSceneTypeDTO;
import com.zjtelcom.cpct.dto.event.EventTypeDTO;

import java.util.List;

/**
 * @Description 事件场景目录service
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventSceneTypeService {

    List<EventSceneTypeDTO> listEventSceneTypes();

    void saveEventSceneType(EventSceneTypeDO eventSceneTypeDO);

    EventSceneTypeDTO getEventSceneTypeDTOById(Long evtSceneTypeId);

    void updateEventSceneType(EventSceneTypeDO eventSceneTypeDO);

    void delEventSceneType(Long evtSceneTypeId);
}
