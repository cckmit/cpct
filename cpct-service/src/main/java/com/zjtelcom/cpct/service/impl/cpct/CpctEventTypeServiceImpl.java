package com.zjtelcom.cpct.service.impl.cpct;

import com.zjtelcom.cpct.dao.event.ContactEvtTypeMapper;
import com.zjtelcom.cpct.dto.event.Catalog;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.dto.pojo.*;
import com.zjtelcom.cpct.service.cpct.CpctEventTypeService;
import com.zjtelcom.cpct.util.CpcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public class CpctEventTypeServiceImpl implements CpctEventTypeService {

    @Autowired
    private ContactEvtTypeMapper eventTypeMapper;

    @Override
    public CpcGroupResponse createEventCatalogJt(CpcGroupRequest<CatalogDetailPo> cpcGroupRequest) {
        CpcGroupResponse cpcGroupResponse = null;
        ContractReqRoot<CatalogDetailPo> contractRoot = cpcGroupRequest.getContractRoot();
        SvcReqCont<CatalogDetailPo> svcCont = contractRoot.getSvcCont();
        CatalogDetailPo catalogDetailPo = svcCont.getRequestObject();
        List<CatalogDetail> catalogDetails = catalogDetailPo.getEventCatalogDetails();
        if(null != catalogDetails){
            for (CatalogDetail catalogDetail : catalogDetails) {
                //todo catalog 转 eventType
                ContactEvtType evtType = new ContactEvtType();
                evtType.setContactEvtName(catalogDetail.getCatalogName());
                evtType.setContactEvtTypeCode(catalogDetail.getCatalogNbr());
                evtType.setEvtTypeDesc(catalogDetail.getCatalogDesc());
                //todo 父级id
                Long parEvtTypeId = catalogDetail.getCatalogItems().get(0).getParCatalogItemId();
                evtType.setParEvtTypeId(parEvtTypeId);
                eventTypeMapper.createContactEvtType(evtType);
            }
        }
        cpcGroupResponse = CpcUtil.buildSuccessResponse(cpcGroupRequest);
        return cpcGroupResponse;
    }

    @Override
    public CpcGroupResponse modEventCatalogJtReq(CpcGroupRequest<CatalogDetailPo> cpcGroupRequest) {
        CpcGroupResponse cpcGroupResponse = null;
        ContractReqRoot<CatalogDetailPo> contractRoot = cpcGroupRequest.getContractRoot();
        SvcReqCont<CatalogDetailPo> svcCont = contractRoot.getSvcCont();
        CatalogDetailPo catalogDetailPo = svcCont.getRequestObject();
        List<CatalogDetail> catalogDetails = catalogDetailPo.getEventCatalogDetails();
        if(null != catalogDetails){
            for (int i = 0; i < catalogDetails.size(); i++) {
                CatalogDetail catalogDetail = catalogDetails.get(i);
                Catalog catalog = catalogDetail;
                String catalogActType = catalogDetail.getActType();
                if (ActType.MOD.equals(catalogActType)) {
                    //todo catalog 转 eventType
                    ContactEvtType evtType = new ContactEvtType();
                    eventTypeMapper.modContactEvtType(evtType);
                } else if (ActType.DEL.equals(catalogActType)) {
                    eventTypeMapper.deleteByPrimaryKey(catalog.getCatalogId());
                }
            }
        }
        cpcGroupResponse = CpcUtil.buildSuccessResponse(cpcGroupRequest);
        return cpcGroupResponse;
    }

}
