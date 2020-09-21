package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.EventRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.MktOfferEventMapper;
import com.zjtelcom.cpct.domain.channel.EventRel;
import com.zjtelcom.cpct.domain.event.MktOfferEventDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.service.channel.EventRelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.crossstore.HashMapChangeSet;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class EventRelServiceImpl implements EventRelService {

    Logger log = LoggerFactory.getLogger(EventRelServiceImpl.class);

    @Autowired
    private EventRelMapper eventRelMapper;
    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private RedisUtils redisUtils;
    /*
    ** 获取未被当前事件关联的事件
    ** aEvtId-被关联的事件 zEvtId-进行关联操作的事件
    */
    @Override
    public Map<String,Object> getEventNoRelation(Long userId, ContactEvt contactEvt) {
        Map<String,Object> resultMap = new HashMap<>();
        List<ContactEvt> eventList = new ArrayList<>();
        List<Long> delEventRelList = new ArrayList<>();
        List<ContactEvt> contactEvtList = contactEvtMapper.query();
        List<EventRel> eventRelList = eventRelMapper.selectByZEvtId(contactEvt.getContactEvtId());

        for(int i = 0;i<eventRelList.size();i++) {
            delEventRelList.add(eventRelList.get(i).getaEvtId());
        }
        delEventRelList.add(contactEvt.getContactEvtId());
        for(int i = 0;i<contactEvtList.size();i++) {
            if(!delEventRelList.contains(contactEvtList.get(i).getContactEvtId())) {
                eventList.add(contactEvtList.get(i));
            }
        }
        resultMap.put("resultCode", CODE_SUCCESS);
        resultMap.put("resultMsg", eventList);
        return resultMap;
    }

    @Override
    public Map<String,Object> createEventRelation(Long userId, EventRel addVO) {
        Map<String,Object> resultMap = new HashMap<>();
        EventRel eventRel = BeanUtil.create(addVO, new EventRel());
        eventRel.setSort(1L);
        eventRel.setCreateDate(DateUtil.getCurrentTime());
        eventRel.setUpdateDate(DateUtil.getCurrentTime());
        eventRel.setStatusDate(DateUtil.getCurrentTime());
        eventRel.setUpdateStaff(userId);
        eventRel.setCreateStaff(userId);
        eventRel.setStatusCd(CommonConstant.STATUSCD_EFFECTIVE);
        eventRelMapper.insert(eventRel);
        resultMap.put("resultCode", CODE_SUCCESS);
        resultMap.put("resultMsg", "添加成功");
        return resultMap;
    }

    /*
     ** 删除关联关系
     */
    @Override
    public Map<String,Object> delEventRelation(Long userId, EventRel delVO) {
        Map<String,Object> resultMap = new HashMap<>();
        EventRel eventRel = eventRelMapper.selectByPrimaryKey(delVO.getComplexEvtRelaId());
        if(null == eventRel) {
            resultMap.put("resultCode", CODE_FAIL);
            resultMap.put("resultMsg", "被关联的事件不存在");
        }
        eventRelMapper.deleteByPrimaryKey(delVO.getComplexEvtRelaId());
        resultMap.put("resultCode", CODE_SUCCESS);
        resultMap.put("resultMsg", "删除成功");
        return resultMap;
    }


    /*
     ** 获取被关联的事件列表
     */
    @Override
    public Map<String,Object> getEventRelList(Long userId, ContactEvt contactEvt) {
        Map<String,Object> resultMap = new HashMap<>();
        List<EventRel> eventRelList = eventRelMapper.selectByZEvtId(contactEvt.getContactEvtId());
        resultMap.put("resultCode", CODE_SUCCESS);
        resultMap.put("resultMsg", eventRelList);
        return resultMap;
    }

    @Autowired
    private MktOfferEventMapper mktOfferEventMapper;

    @Override
    public Map<String, Object> getEventListByOffer(Map<String, Object> paramMap) {
        List<String> offerCodeList = (List<String>) paramMap.get("offerCodeList");
        String eventType = (String)paramMap.get("eventType");
        log.info("入参：offerCodeList" + offerCodeList);
        log.info("入参：eventType" + eventType);
        HashMap<String,Object> result = new HashMap<>();
//        HashMap<String,List> dataMap = new HashMap<String,List>();
        List<Map<String,Object>> data = new ArrayList<>();
        try{
            for(String offerCode :offerCodeList){
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                //先取缓存
                if(redisUtils.get("OFFER_EVENT_LIST" + offerCode) != null){
                    List<Map<String,Object>> eventList = (List<Map<String,Object>>)redisUtils.get("OFFER_EVENT_LIST" + offerCode);
                    dataMap.put("offerCode", offerCode);
                    dataMap.put("eventList",eventList);
                    data.add(dataMap);
                    log.info("销售品获取关联事件数据走缓存：" + offerCode);
                    continue;
                };

                //否则取数据库
                List<MktOfferEventDO> mktOfferEventDOList = mktOfferEventMapper.getEventIdByOfferNbr(offerCode,Integer.parseInt(eventType));
                log.info(" 数据库返回：mktOfferEventDOList" + mktOfferEventDOList);

                if(mktOfferEventDOList.size() == 0){
                    List<String> eventList = new ArrayList<>();
                    dataMap.put("offerCode", offerCode);
                    dataMap.put("eventList",eventList);
                    data.add(dataMap);
                    continue;
                }else {
                    dataMap.put("offerCode",offerCode);
                    List<Map<String,Object>> eventList = new ArrayList<>();
                    for (MktOfferEventDO mktOfferEventDO : mktOfferEventDOList){
                        Map<String,Object> eventMap = new HashMap();
                        if(mktOfferEventDO.getEventNbr() != null){
                            eventMap.put("eventCode",mktOfferEventDO.getEventNbr());
                            List<String> evtItemCodeList = new ArrayList<>();

                            if(mktOfferEventDO.getEventId() != null){
                                long eventId = mktOfferEventDO.getEventId();
                                evtItemCodeList = contactEvtItemMapper.selectEvtItemCodeByEventId(eventId);
                            }
                            eventMap.put("evtItemCodeList",evtItemCodeList);
                        }
                        eventList.add(eventMap);
                    }
                    dataMap.put("eventList",eventList);
                    data.add(dataMap);
                    redisUtils.setRedisUnit("OFFER_EVENT_LIST" + offerCode, eventList, 86400);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","获取销售品对应事件列表失败");
            result.put("data",e);
            return result;
        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","获取成功");
        result.put("data",data);
        log.info(" 结果：result" + result);
        return result;
    }

}
