package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.DO.EventSceneDO;

import java.util.List;

/**
 * @Description EventSceneService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventSceneService {

    List<EventSceneDO> listSceneEvents(EventSceneDO eveneSceneDO);

    void saveEventScene(EventSceneDO eveneSceneDO);

    EventSceneDO editEventScene(Long eventSceneId);

    void updateEventScene(EventSceneDO eveneSceneDO);

}
