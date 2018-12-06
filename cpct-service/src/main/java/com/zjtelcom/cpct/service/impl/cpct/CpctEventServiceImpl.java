package com.zjtelcom.cpct.service.impl.cpct;

import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMatchRulMapper;
import com.zjtelcom.cpct.dao.event.InterfaceCfgMapper;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.event.ContactEvtMatchRul;
import com.zjtelcom.cpct.dto.pojo.*;
import com.zjtelcom.cpct.service.cpct.CpctEventService;
import com.zjtelcom.cpct.util.CpcUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CpctEventServiceImpl implements CpctEventService {

    @Autowired
    private ContactEvtMapper groupEventMapper;
    @Autowired
    private ContactEvtItemMapper evtItemMapper;
    @Autowired
    private ContactEvtMatchRulMapper evtMatchRulMapper;
    @Autowired
    private InterfaceCfgMapper interfaceCfgMapper;



    @Override
    public CpcGroupResponse createContactEvtJt(CpcGroupRequest<EventPo> cpcGroupRequest) {

        CpcGroupResponse cpcGroupResponse = null;
        ContractReqRoot<EventPo> contractReqRoot = cpcGroupRequest.getContractRoot();

        SvcReqCont<EventPo> svcCont = contractReqRoot.getSvcCont();
        EventPo eventPos = svcCont.getRequestObject();
        EventDetail evtDetails = eventPos.getContactEvtDetails();
        if (evtDetails != null) {
//            for (EventDetail tempEvtDetail : evtDetails) {
                ContactEvt contactEvt = evtDetails;
                groupEventMapper.createContactEvt(contactEvt);
                // 事件采集项
                List<ContactEvtItem> contactEvtItemList = evtDetails.getContactEvtItems();
                if (null != contactEvtItemList) {
                    for (ContactEvtItem tempContactEvtItem : contactEvtItemList) {
                        evtItemMapper.insertContactEvtItem(tempContactEvtItem);
                    }
                }
                // 事件匹配规则
                List<ContactEvtMatchRul> contactEvtMatchRulList = evtDetails.getContactEvtMatchRuls();
                if (null != contactEvtMatchRulList) {
                    for (ContactEvtMatchRul tempContactEvtMatchRul : contactEvtMatchRulList) {
                        evtMatchRulMapper.createContactEvtMatchRul(tempContactEvtMatchRul);
                    }
                }
        }
        cpcGroupResponse = CpcUtil.buildSuccessResponse(cpcGroupRequest);
        return cpcGroupResponse;
    }

    @Override
    public CpcGroupResponse modContactEvtJt(CpcGroupRequest<EventPo> cpcGroupRequest) {
        CpcGroupResponse cpcGroupResponse = null;
        ContractReqRoot<EventPo> contractReqRoot = cpcGroupRequest.getContractRoot();
        SvcReqCont<EventPo> svcCont = contractReqRoot.getSvcCont();
        EventPo eventPos = svcCont.getRequestObject();
        EventDetail evtDetails = eventPos.getContactEvtDetails();
        if (evtDetails != null) {
//            for (EventDetail tempEvtDetail : evtDetails) {
                boolean deleteFlag = false;
                ContactEvt contactEvt = evtDetails;
                String actType = evtDetails.getActType();
                if (null == actType) {
                    //continue;
                } else if (ActType.ADD.equals(actType)) {
                    groupEventMapper.createContactEvt(contactEvt);
                } else if (ActType.DEL.equals(actType)) {
                    deleteFlag = true;
                } else if (ActType.MOD.equals(actType)) {
                    groupEventMapper.modContactEvt(contactEvt);
                }
                // 事件采集项
                List<ContactEvtItem> contactEvtItemList = evtDetails.getContactEvtItems();
                if (null != contactEvtItemList) {
                    for (ContactEvtItem tempContactEvtItem : contactEvtItemList) {
                        String itemActType = tempContactEvtItem.getActType();
                        if (null == itemActType) {
                            continue;
                        } else if (ActType.ADD.equals(itemActType)) {
                            evtItemMapper.insertContactEvtItem(tempContactEvtItem);
                        } else if (ActType.DEL.equals(itemActType)) {
                            evtItemMapper.deleteByPrimaryKey(tempContactEvtItem.getEvtItemId());
                        } else if (ActType.MOD.equals(itemActType)) {
                            evtItemMapper.modEventItem(tempContactEvtItem);
                        }
                    }
                }
                // 事件匹配规则
                List<ContactEvtMatchRul> contactEvtMatchRulList = evtDetails.getContactEvtMatchRuls();
                if (null != contactEvtMatchRulList) {
                    for (ContactEvtMatchRul tempContactEvtMatchRul : contactEvtMatchRulList) {
                        String matchRulActType = tempContactEvtMatchRul.getActType();
                        if (null == matchRulActType) {
                            continue;
                        } else if (ActType.ADD.equals(matchRulActType)) {
                            evtMatchRulMapper.createContactEvtMatchRul(tempContactEvtMatchRul);
                        } else if (ActType.DEL.equals(matchRulActType)) {
                            evtMatchRulMapper.deleteByPrimaryKey(tempContactEvtMatchRul.getEvtMatchRulId());
                        } else if (ActType.MOD.equals(matchRulActType)) {
                            evtMatchRulMapper.modContactEvtMatchRul(tempContactEvtMatchRul);
                        }
                    }
                }
//                // 事件触发规则
//                List<ContactEvtTrigRul> contactEvtTrigRulList = tempEvtDetail.getContactEvtTrigRuls();
//                if (null != contactEvtTrigRulList) {
//                    for (ContactEvtTrigRul tempContactEvtTrigRul : contactEvtTrigRulList) {
//                        String trigRulActType = tempContactEvtTrigRul.getActType();
//                        if (null == trigRulActType) {
//                            continue;
//                        } else if (ActType.ADD.equals(trigRulActType)) {
//                            groupEventMapper.insertTrigRulSelective(tempContactEvtTrigRul);
//                        } else if (ActType.DEL.equals(trigRulActType)) {
//                            groupEventMapper.deleteTrigRulByPrimaryKey(tempContactEvtTrigRul.getEvtTrigRulId());
//                        } else if (ActType.MOD.equals(trigRulActType)) {
//                            groupEventMapper.updateTrigRulSelective(tempContactEvtTrigRul);
//                        }
//                    }
//                }
//                // 事件源接口配置
//                InterfaceCfgDetail interfaceCfgDetail = tempEvtDetail.getInterfaceCfgDetail();
//                if (null != interfaceCfgDetail) {
//                    boolean interfaceCfgDelFlag = false;
//                    String interfaceActType = interfaceCfgDetail.getActType();
//                    if (null == interfaceActType) {
//                        continue;
//                    } else if (ActType.ADD.equals(interfaceActType)) {
//                        groupEventMapper.insertInterfaceCfgSelective(interfaceCfgDetail);
//                    } else if (ActType.DEL.equals(interfaceActType)) {
//                        interfaceCfgDelFlag = true;
//                    } else if (ActType.MOD.equals(interfaceActType)) {
//                        groupEventMapper.updateInterfaceCfgSelective(interfaceCfgDetail);
//                    }
//
//                    List<InterfaceCfgParam> interfaceCfgParams = interfaceCfgDetail.getInterfaceCfgParams();
//                    if (null != interfaceCfgParams) {
//                        for (InterfaceCfgParam tempInterfaceCfgParam : interfaceCfgParams) {
//                            String cfgParamActType = tempInterfaceCfgParam.getActType();
//                            if (null == cfgParamActType) {
//                                continue;
//                            } else if (ActType.ADD.equals(cfgParamActType)) {
//                                groupEventMapper.insertInterfaceCfgParamSelective(tempInterfaceCfgParam);
//                            } else if (ActType.DEL.equals(cfgParamActType)) {
//                                groupEventMapper.deleteInterfaceCfgParamByPrimaryKey(tempInterfaceCfgParam
//                                        .getInterfaceCfgParamId());
//                            } else if (ActType.MOD.equals(cfgParamActType)) {
//                                groupEventMapper.updateInterfaceCfgParamSelective(tempInterfaceCfgParam);
//                            }
//                        }
//                    }
//                    if (interfaceCfgDelFlag) {
//                        groupEventMapper.deleteInterfaceCfgByPrimaryKey(interfaceCfgDetail.getInterfaceCfgId());
//                    }
//                }
//                if (deleteFlag) {
//                    groupEventMapper.deleteByPrimaryKey(contactEvt.getContactEvtId());
//                }
            }
        cpcGroupResponse = CpcUtil.buildSuccessResponse(cpcGroupRequest);
        return cpcGroupResponse;
    }
}
