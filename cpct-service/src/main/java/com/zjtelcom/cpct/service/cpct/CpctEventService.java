package com.zjtelcom.cpct.service.cpct;

import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;
import com.zjtelcom.cpct.dto.pojo.EventPo;

public interface CpctEventService  {

    CpcGroupResponse createContactEvtJt(CpcGroupRequest<EventPo> cpcGroupRequest);

    CpcGroupResponse modContactEvtJt(CpcGroupRequest<EventPo> cpcGroupRequest);

}
