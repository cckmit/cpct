package com.zjtelcom.cpct.service.event;

import com.zjtelcom.cpct.domain.event.EventSceneDO;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.request.event.*;
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

    Map<String,Object> editEventScene(Long eventSceneId) throws Exception;

    Map<String,Object> modEventScene(ModEventSceneReq modEventSceneReq);

    Map<String,Object> delEventScene(EventScene eventScene);

    Map<String,Object> createEventSceneJt(CreateEventSceneJtReq createEventSceneJtReq);

    Map<String,Object> modEventSceneJt(ModEventSceneJtReq modEventSceneJtReq);

    Map<String,Object> coEventScene(EventScene eventScene);

}
