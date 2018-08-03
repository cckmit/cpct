package com.zjtelcom.cpct.service.cpct;

import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;
import com.zjtelcom.cpct.dto.pojo.EventScenePo;

public interface CpctEventSceneService {

    CpcGroupResponse createEventSceneJt(CpcGroupRequest<EventScenePo> cpcGroupRequest);

    CpcGroupResponse modEventSceneJt(CpcGroupRequest<EventScenePo> cpcGroupRequest);

}
