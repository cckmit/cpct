package com.zjtelcom.cpct.service.impl.event;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.channel.InjectionLabelValueMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulConditionMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulMapper;
import com.zjtelcom.cpct.dao.org.OrgTreeMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.domain.channel.LabelValue;
import com.zjtelcom.cpct.domain.org.OrgTreeDO;
import com.zjtelcom.cpct.dto.channel.LabelValueVO;
import com.zjtelcom.cpct.dto.channel.OperatorDetail;
import com.zjtelcom.cpct.dto.event.*;
import com.zjtelcom.cpct.dto.grouping.SysAreaVO;
import com.zjtelcom.cpct.enums.LeftParamType;
import com.zjtelcom.cpct.enums.Operator;
import com.zjtelcom.cpct.enums.RightParamType;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.EventMatchRulService;
import com.zjtelcom.cpct.util.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class EventMatchRulServiceImpl extends BaseService implements EventMatchRulService{

    @Autowired
    private EventMatchRulMapper eventMatchRulMapper;
    @Autowired
    private EventMatchRulConditionMapper eventMatchRulConditionMapper;
    @Autowired
    private InjectionLabelMapper injectionLabelMapper;
    @Autowired
    private InjectionLabelValueMapper injectionLabelValueMapper;
    @Autowired
    private OrgTreeMapper orgTreeMapper;
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 新增事件规则
     */
    @Override
    public Map<String, Object> createEventMatchRul(final EventMatchRulDetail eventMatchRulDetail) {
        Map<String, Object> maps = new HashMap<>();
        EventMatchRulDTO eventMatchRulDTO = new EventMatchRulDTO();

        eventMatchRulDTO = eventMatchRulDetail;
        eventMatchRulDTO.setCreateDate(DateUtil.getCurrentTime());
        eventMatchRulDTO.setUpdateDate(DateUtil.getCurrentTime());
        eventMatchRulDTO.setStatusDate(DateUtil.getCurrentTime());
        eventMatchRulDTO.setUpdateStaff(UserUtil.loginId());
        eventMatchRulDTO.setCreateStaff(UserUtil.loginId());
        eventMatchRulDTO.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        eventMatchRulMapper.createEventMatchRul(eventMatchRulDTO);

        List<EventMatchRulCondition> eventMatchRulConditions = eventMatchRulDetail.getEventMatchRulConditions();
        EventMatchRulDTO eventMatchRulDTO1 = eventMatchRulMapper.listEventMatchRul(eventMatchRulDetail.getEventId());
        for(final EventMatchRulCondition eventMatchRulCondition : eventMatchRulConditions) {
            if (eventMatchRulCondition.getOperType() == null || eventMatchRulCondition.getOperType().equals("")) {
                maps.put("resultCode", CommonConstant.CODE_FAIL);
                maps.put("resultMsg", "请选择下拉框运算类型");
                return maps;
            }
//            if (eventMatchRulCondition.getAreaIdList()!=null){
//                area2RedisThread(eventMatchRulDTO, eventMatchRulCondition);
//            }
            eventMatchRulCondition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
            eventMatchRulCondition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
            eventMatchRulCondition.setEvtMatchRulId(eventMatchRulDTO1.getEvtMatchRulId());
            eventMatchRulCondition.setCreateDate(DateUtil.getCurrentTime());
            eventMatchRulCondition.setUpdateDate(DateUtil.getCurrentTime());
            eventMatchRulCondition.setStatusDate(DateUtil.getCurrentTime());
            eventMatchRulCondition.setUpdateStaff(UserUtil.loginId());
            eventMatchRulCondition.setCreateStaff(UserUtil.loginId());
            eventMatchRulCondition.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
            eventMatchRulConditionMapper.insertEventMatchRulCondition(eventMatchRulCondition);
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("tarGrp", eventMatchRulDTO);
        return maps;
    }

//    private void area2RedisThread(EventMatchRulDTO eventMatchRulDTO, final EventMatchRulCondition eventMatchRulCondition) {
//        final Long evtMatchRulId = eventMatchRulDTO.getEvtMatchRulId();
//        List<OrgTreeDO> sysAreaList = new ArrayList<>();
//        for (Integer id : eventMatchRulCondition.getAreaIdList()){
//            OrgTreeDO orgTreeDO = orgTreeMapper.selectByAreaId(id);
//            if (orgTreeDO!=null){
//                sysAreaList.add(orgTreeDO);
//            }
//        }
//        redisUtils.set("AREA_RULE_ENTITY_"+evtMatchRulId,sysAreaList);
//        new Thread() {
//            public void run() {
//                areaList2Redis(evtMatchRulId,eventMatchRulCondition.getAreaIdList());
//            }
//        }.start();
//    }
//
//
//    public void areaList2Redis(Long targrpId,List<Integer> areaIdList){
//        List<String> resultList = new ArrayList<>();
//        List<OrgTreeDO> sysAreaList = new ArrayList<>();
//        for (Integer id : areaIdList){
//            areaList(id,resultList,sysAreaList);
//        }
//        redisUtils.set("AREA_RULE_"+targrpId,resultList);
//    }
//
//    public List<String> areaList(Integer parentId,List<String> resultList,List<OrgTreeDO> areas){
//        List<OrgTreeDO> sysAreaList = orgTreeMapper.selectBySumAreaId(parentId);
//        if (sysAreaList.isEmpty()){
//            return resultList;
//        }
//        for (OrgTreeDO area : sysAreaList){
//            resultList.add(area.getAreaName());
//            areas.add(area);
//            areaList(area.getAreaId(),resultList,areas);
//        }
//        return resultList;
//    }

    /**
     * 修改事件规则
     */
    @Override
    public Map<String, Object> modEventMatchRul(EventMatchRulDetail eventMatchRulDetail) {
        Map<String, Object> maps = new HashMap<>();
        EventMatchRulDTO eventMatchRulDTO = new EventMatchRulDTO();
        eventMatchRulDTO = eventMatchRulDetail;
        eventMatchRulDTO.setUpdateDate(DateUtil.getCurrentTime());
        eventMatchRulDTO.setUpdateStaff(UserUtil.loginId());
        eventMatchRulMapper.modEventMatchRul(eventMatchRulDTO);
        List<EventMatchRulCondition> eventMatchRulConditions = eventMatchRulDetail.getEventMatchRulConditions();
        for (EventMatchRulCondition eventMatchRulCondition : eventMatchRulConditions) {
            EventMatchRulCondition eventMatchRulCondition1 = eventMatchRulConditionMapper.selectByPrimaryKey(eventMatchRulCondition.getConditionId());
            if (eventMatchRulCondition1 == null) {
                if (eventMatchRulCondition.getOperType()==null || eventMatchRulCondition.getOperType().equals("")){
                    maps.put("resultCode", CommonConstant.CODE_FAIL);
                    maps.put("resultMsg", "请选择下拉框运算类型");
                    return maps;
                }
//                if (eventMatchRulCondition.getAreaIdList()!=null){
//                    area2RedisThread(eventMatchRulDTO, eventMatchRulCondition);
//                }
                eventMatchRulCondition.setLeftParamType(LeftParamType.LABEL.getErrorCode());//左参为注智标签
                eventMatchRulCondition.setRightParamType(RightParamType.FIX_VALUE.getErrorCode());//右参为固定值
                eventMatchRulCondition.setEvtMatchRulId(eventMatchRulDTO.getEvtMatchRulId());
                eventMatchRulCondition.setUpdateDate(DateUtil.getCurrentTime());
                eventMatchRulCondition.setCreateDate(DateUtil.getCurrentTime());
                eventMatchRulCondition.setStatusDate(DateUtil.getCurrentTime());
                eventMatchRulCondition.setUpdateStaff(UserUtil.loginId());
                eventMatchRulCondition.setCreateStaff(UserUtil.loginId());
                eventMatchRulCondition.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
                eventMatchRulConditionMapper.insertEventMatchRulCondition(eventMatchRulCondition);
            } else {
                eventMatchRulCondition.setUpdateDate(DateUtil.getCurrentTime());
                eventMatchRulCondition.setUpdateStaff(UserUtil.loginId());
                eventMatchRulConditionMapper.modEventMatchRulCondition(eventMatchRulCondition);
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 删除事件规则
     */
    @Override
    public Map<String, Object> delEventMatchRul(EventMatchRulDetail eventMatchRulDetail) {
        Map<String, Object> maps = new HashMap<>();
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulDetail;
        eventMatchRulMapper.delEventMatchRul(eventMatchRulDTO);
        List<EventMatchRulCondition> eventMatchRulConditions = eventMatchRulDetail.getEventMatchRulConditions();
        for (EventMatchRulCondition eventMatchRulCondition : eventMatchRulConditions) {
            eventMatchRulConditionMapper.delEventMatchRulCondition(eventMatchRulCondition);
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 获取事件规则
     */
    @Override
    public Map<String, Object> listEventMatchRul(Long eventId) {
        Map<String, Object> maps = new HashMap<>();
        if (eventId == null) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "");
            return maps;
        }
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(eventId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("listEventMatchRul", eventMatchRulDTO);
        return maps;
    }

    /**
     * 删除事件规则条件
     */
    @Override
    public Map<String, Object> delEventMatchRulCondition(EventMatchRulCondition eventMatchRulCondition) {
        Map<String, Object> maps = new HashMap<>();
        eventMatchRulConditionMapper.delEventMatchRulCondition(eventMatchRulCondition);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 获取事件规则条件信息
     */
    @Override
    public Map<String, Object> listEventMatchRulCondition(Long evtMatchRulId) throws Exception {
        Map<String, Object> maps = new HashMap<>();
        if (evtMatchRulId == null) {
            maps.put("resultCode", CommonConstant.CODE_FAIL);
            maps.put("resultMsg", "");
            return maps;
        }

        List<EventMatchRulCondition> listEventMatchRulCondition = eventMatchRulConditionMapper.listEventMatchRulCondition(evtMatchRulId);
        List<EventMatchRulConditionVO> rulConditionList = new ArrayList<>();
        List<EventMatchRulVO> EventMatchRulVOS = new ArrayList<>();//传回前端展示信息
        for (EventMatchRulCondition eventMatchRulCondition : listEventMatchRulCondition) {
            List<OperatorDetail> operatorList = new ArrayList<>();
            EventMatchRulConditionVO eventMatchRulConditionVO = new EventMatchRulConditionVO();
            CopyPropertiesUtil.copyBean2Bean(eventMatchRulConditionVO, eventMatchRulCondition);
            //塞入左参中文名
            Label label = injectionLabelMapper.selectByPrimaryKey(Long.valueOf(eventMatchRulConditionVO.getLeftParam()));
            if (label == null){
                continue;
            }
            List<LabelValue> labelValues = injectionLabelValueMapper.selectByLabelId(label.getInjectionLabelId());
            List<LabelValueVO> valueList = ChannelUtil.valueList2VOList(labelValues);
            eventMatchRulConditionVO.setLeftParamName(label.getInjectionLabelName());
            //塞入领域
//            FitDomain fitDomain = null;
//            if (label.getFitDomain() != null) {
//                fitDomain = FitDomain.getFitDomain(Integer.parseInt(label.getFitDomain()));
//                tarGrpConditionVO.setFitDomainId(Long.valueOf(fitDomain.getValue()));
//                tarGrpConditionVO.setFitDomainName(fitDomain.getDescription());
//            }
            //将操作符转为中文
            if (eventMatchRulConditionVO.getOperType()!=null && !eventMatchRulConditionVO.getOperType().equals("")){
                Operator op = Operator.getOperator(Integer.parseInt(eventMatchRulConditionVO.getOperType()));
                eventMatchRulConditionVO.setOperTypeName(op.getDescription());
            }
            //todo 通过左参id
            String operators = label.getOperator();
            String[] operator = operators.split(",");
            if (operator.length > 1) {
                for (int i = 0; i < operator.length; i++) {
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[i]));
                    OperatorDetail operatorDetail = new OperatorDetail();
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            } else {
                if (operator.length == 1) {
                    OperatorDetail operatorDetail = new OperatorDetail();
                    Operator opTT = Operator.getOperator(Integer.parseInt(operator[0]));
                    operatorDetail.setOperName(opTT.getDescription());
                    operatorDetail.setOperValue(opTT.getValue());
                    operatorList.add(operatorDetail);
                }
            }
            eventMatchRulConditionVO.setConditionType(label.getConditionType());
            eventMatchRulConditionVO.setValueList(valueList);
            eventMatchRulConditionVO.setOperatorList(operatorList);
            rulConditionList.add(eventMatchRulConditionVO);
        }
//        List<OrgTreeDO> sysAreaList = (List<OrgTreeDO>)redisUtils.get("AREA_RULE_ENTITY_"+evtMatchRulId);
//        if (sysAreaList!=null){
//            List<SysAreaVO> voList = new ArrayList<>();
//            for (OrgTreeDO area : sysAreaList){
//                SysAreaVO vo = BeanUtil.create(area,new SysAreaVO());
//                voList.add(vo);
//            }
//            EventMatchRulConditionVO eventMatchRulConditionVO = new EventMatchRulConditionVO();
//
//            eventMatchRulConditionVO.setSysAreaList(voList);
//            rulConditionList.add(eventMatchRulConditionVO);
//        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        maps.put("listEventMatchRulCondition", rulConditionList);
        return maps;
    }
}
