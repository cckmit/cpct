package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.request.event.CreateEventSceneReq;
import com.zjtelcom.cpct.request.event.ModEventSceneReq;
import com.zjtelcom.cpct.request.event.QryEventSceneListReq;
import com.zjtelcom.cpct.response.event.QryeventSceneRsp;
import java.util.Map;

/**
 * @Description EventSceneService
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface EventSceneService {

    QryeventSceneRsp qryEventSceneList(QryEventSceneListReq qryEventSceneListReq);

    Map<String,Object> createEventScene(CreateEventSceneReq createEventSceneReq);

    EventSceneDO editEventScene(Long eventSceneId);

    Map<String,Object> modEventScene(ModEventSceneReq modEventSceneReq);

    Map<String,Object> delEventScene(EventScene eventScene);

}
