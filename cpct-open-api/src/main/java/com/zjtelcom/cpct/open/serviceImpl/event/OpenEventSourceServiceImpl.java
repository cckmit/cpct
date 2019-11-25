package com.zjtelcom.cpct.open.serviceImpl.event;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.event.EventSorceMapper;
import com.zjtelcom.cpct.domain.event.EventSorceDO;
import com.zjtelcom.cpct.domain.event.InterfaceCfg;
import com.zjtelcom.cpct.dto.event.EventSorce;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.open.base.common.CommonUtil;
import com.zjtelcom.cpct.open.base.common.FormatBeanUtil;
import com.zjtelcom.cpct.open.base.service.BaseService;
import com.zjtelcom.cpct.open.entity.event.EventSource;
import com.zjtelcom.cpct.open.entity.event.EventSourceInterface;
import com.zjtelcom.cpct.open.service.event.OpenEventSourceService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.UserUtil;
import com.zjtelcom.cpct.dao.event.InterfaceCfgMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @Auther: anson
 * @Date: 2018/10/26
 * @Description:
 */
@Service
@Transactional
public class OpenEventSourceServiceImpl extends BaseService implements OpenEventSourceService {

    @Autowired
    private EventSorceMapper eventSorceMapper;

    @Autowired
    private InterfaceCfgMapper interfaceCfgMapper;

    /**
     * 新增事件源
     *
     * @param eventSorce
     * @return
     */
    @Override
    public Map<String, Object> saveEventSorce(EventSorce eventSorce) {
        Map<String, Object> eventSorceMap = new HashMap<>();
            EventSorceDO eventSorceDO = BeanUtil.create(eventSorce, new EventSorceDO());
            eventSorceDO.setEvtSrcCode("ERC" + DateUtil.date2String(new Date()) + ChannelUtil.getRandomStr(4));
            eventSorceDO.setCreateStaff(UserUtil.loginId());
            eventSorceDO.setCreateDate(new Date());
            eventSorceDO.setUpdateStaff(UserUtil.loginId());
            eventSorceDO.setUpdateDate(new Date());
            eventSorceMapper.insert(eventSorceDO);
            eventSorceMap.put("params", eventSorceDO);
        return eventSorceMap;
    }

    /**
     * 查询事件源
     *
     * @param evtSrcId
     * @return
     */
    @Override
    public Map<String, Object> getEventSorce(Long evtSrcId) {
        Map<String, Object> eventSorceMap = new HashMap<>();
        EventSorceDO eventSorceDO = eventSorceMapper.selectByPrimaryKey(evtSrcId);
        if (null != eventSorceDO) {
            Map<String, Object> map = FormatBeanUtil.objectToMap(eventSorceDO);
            EventSource eventSorce = BeanUtil.create(eventSorceDO, new EventSource());
            //statusDate  转成对应格式
            map.put("statusDate",DateUtil.TimeStampToNum((Long) map.get("statusDate")));
            map.put("id", "125");
            map.put("href", "/eventSorce/" + evtSrcId);
            map.remove("updateDate");
            map.remove("lanId");
            map.remove("createStaff");
            map.remove("createDate");
            map.remove("updateStaff");
            // 事件源接口中有关联事件源主键EVT_SRC_ID   集团openapi中InterfaceCfgParam信息在项目中没有采用无需返回
            List<InterfaceCfg> interfaceCfgListByParam = interfaceCfgMapper.findInterfaceCfgListByParam(evtSrcId, null, null,null);
            // 转化成集团openapi要求规范
            List<EventSourceInterface> list=new ArrayList<>();
            for (InterfaceCfg interfaceCfg:interfaceCfgListByParam){
                EventSourceInterface eventSourceInterface = BeanUtil.create(interfaceCfg, new EventSourceInterface());
                eventSourceInterface.setInterfaceCode(interfaceCfg.getInterfaceNbr());
                if(null!=interfaceCfg.getStatusDate()){
                    eventSourceInterface.setStatusDate(DateUtil.getDatetime(interfaceCfg.getStatusDate()));
                }
                list.add(eventSourceInterface);
            }
            map.put("interfaceCfg", list);
            eventSorceMap.put("params", JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue));
        } else {
            throw new SystemException("事件源id为" + evtSrcId + " 所对应的事件源不存在!");
        }

        return eventSorceMap;
    }

    /**
     * 更新事件源
     *
     * @param evtSrcId 事件源主键
     * @param params   需要更新的内容
     * @return params参数样文
     * [
     * { "op": "replace", "path":"/eventSceneName", "value": "流量超出事件场景A"},
     * { "op": "replace", "path": "/eventSceneDesc", "value": "流量超出事件场景B"}
     * ]
     * op类型包括  test,remove,add,replace,move,copy
     */
    @Override
    public Map<String, Object> updateEventSorce(String evtSrcId, String params) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Map<String, Object> eventSorceMap = new HashMap<>();
        EventSorceDO eventSorceDO = eventSorceMapper.selectByPrimaryKey(Long.valueOf(evtSrcId));
        if (null == eventSorceDO) {
            throw new SystemException("对应事件源不存在");
        }
        Map<String, Object> stringObjectMap = FormatBeanUtil.objectToMap(eventSorceDO);
        //开始判断需要修改的字段  params应该是一个jsonArray
        JSONArray array = (JSONArray) JSONArray.parse(params);
        for (int i = 0; i <array.size() ; i++) {
            //目前只考虑修改值的情况
            JSONObject jsonObject = (JSONObject) array.get(i);
            String path = (String) jsonObject.get("path");
            stringObjectMap.put(path.substring(1),jsonObject.getString("value"));
        }
        EventSorceDO source = FormatBeanUtil.mapToObject(stringObjectMap, EventSorceDO.class);
        eventSorceMapper.updateByPrimaryKey(source);
        eventSorceMap.put("params",source);
        return eventSorceMap;
    }

    /**
     * 删除事件源
     *
     * @param evtSrcId
     * @return
     */
    @Override
    public Map<String, Object> deleteEventSorce(Long evtSrcId) {
        Map<String, Object> eventSorceMap = new HashMap<>();
        int i = eventSorceMapper.deleteByPrimaryKey(evtSrcId);
        if(i==0){
            throw new SystemException("对应事件源不存在");
        }
        return eventSorceMap;
    }

    /**
     * 分页查询事件源列表
     *
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> listEventSorcePage(Map<String, Object> params) {
        Map<String, Object> eventSorceMap = new HashMap<>();
            EventSorceDO eventSorceDOReq = new EventSorceDO();
            if(StringUtils.isNotBlank((String)params.get("evtSrcId"))){
                eventSorceDOReq.setEvtSrcId(Long.valueOf((String)params.get("evtSrcId")));
            }
            eventSorceDOReq.setEvtSrcCode((String) params.get("evtSrcCode"));
            eventSorceDOReq.setEvtSrcName((String) params.get("evtSrcName"));
            eventSorceDOReq.setStatusCd((String) params.get("statusCd"));
            CommonUtil.setPage(params);
            List<EventSorceDO> eventSorceDOList = eventSorceMapper.selectForPage(eventSorceDOReq);
            //处理成集团规范返回格式
            List<EventSource> list=new ArrayList<>();
            for(EventSorceDO eventSorceDO:eventSorceDOList){
                EventSource eventSorce = BeanUtil.create(eventSorceDO, new EventSource());
                eventSorce.setId(eventSorceDO.getEvtSrcId().toString());
                eventSorce.setHref("/eventSorces");
                if(null!=eventSorceDO.getStatusDate()){
                    eventSorce.setStatusDate(DateUtil.getDatetime(eventSorceDO.getStatusDate()));
                }
                list.add(eventSorce);
            }
            Page pageInfo = new Page(new PageInfo(eventSorceDOList));
            eventSorceMap.put("params", list);
            eventSorceMap.put("size", String.valueOf(pageInfo.getTotal()));
        return eventSorceMap;
    }




}
