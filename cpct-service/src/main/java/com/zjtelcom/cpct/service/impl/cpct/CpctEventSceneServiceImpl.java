package com.zjtelcom.cpct.service.impl.cpct;

import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.dto.event.EventSceneDetail;
import com.zjtelcom.cpct.dto.pojo.*;
import com.zjtelcom.cpct.service.cpct.CpctEventSceneService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.CpcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CpctEventSceneServiceImpl implements CpctEventSceneService {

    @Autowired
    private EventSceneMapper eventSceneMapper;


    @Override
    public CpcGroupResponse createEventSceneJt(CpcGroupRequest<EventScenePo> cpcGroupRequest) {
        CpcGroupResponse cpcGroupResponse = null;
        ContractReqRoot<EventScenePo> contractReqRoot = cpcGroupRequest.getContractRoot();
        SvcReqCont<EventScenePo> svcCont = contractReqRoot.getSvcCont();
        EventScenePo eventScenePos = svcCont.getRequestObject();
        List<EventSceneDetail> eventSceneDetails = eventScenePos.getEventSceneDetails();
        if (null != eventSceneDetails) {
            for (int i = 0; i < eventSceneDetails.size(); i++) {
                EventSceneDetail eventSceneDetail = eventSceneDetails.get(i);
                EventScene eventScene = BeanUtil.create(eventSceneDetail,new EventScene());
                //todo 事件场景编码自动生成
                eventScene.setContactEvtCode("");
                eventScene.setEventSceneNbr("ET0990766987");
                eventSceneMapper.insert(eventScene);
            }
        }
        cpcGroupResponse = CpcUtil.buildSuccessResponse(cpcGroupRequest);
        return cpcGroupResponse;

    }

    @Override
    public CpcGroupResponse modEventSceneJt(CpcGroupRequest<EventScenePo> cpcGroupRequest) {
        CpcGroupResponse cpcGroupResponse = null;
        ContractReqRoot<EventScenePo> contractReqRoot = cpcGroupRequest.getContractRoot();
        SvcReqCont<EventScenePo> svcCont = contractReqRoot.getSvcCont();
        EventScenePo eventScenePos = svcCont.getRequestObject();
        List<EventSceneDetail> eventSceneDetails = eventScenePos.getEventSceneDetails();
        if (null != eventSceneDetails) {
            for (int i = 0; i < eventSceneDetails.size(); i++) {
                EventSceneDetail eventSceneDetail = eventSceneDetails.get(i);
                EventScene eventScene = BeanUtil.create(eventSceneDetail,new EventScene());
                String type = eventSceneDetail.getActType();
                if(ActType.MOD.equals(type)){
                    eventSceneMapper.updateByPrimaryKey(eventScene);
                }else if(ActType.DEL.equals(type)){
                    eventSceneMapper.deleteByPrimaryKey(eventScene.getEventSceneId());
                }
            }
        }
        cpcGroupResponse = CpcUtil.buildSuccessResponse(cpcGroupRequest);
        return cpcGroupResponse;
    }
}
