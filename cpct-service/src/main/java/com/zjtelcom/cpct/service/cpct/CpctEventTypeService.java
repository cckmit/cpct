package com.zjtelcom.cpct.service.cpct;

import com.zjtelcom.cpct.dto.pojo.CatalogDetailPo;
import com.zjtelcom.cpct.dto.pojo.CpcGroupRequest;
import com.zjtelcom.cpct.dto.pojo.CpcGroupResponse;

public interface CpctEventTypeService {

    CpcGroupResponse createEventCatalogJt(CpcGroupRequest<CatalogDetailPo> cpcGroupRequest);

    CpcGroupResponse modEventCatalogJtReq(CpcGroupRequest<CatalogDetailPo> cpcGroupRequest);

}
