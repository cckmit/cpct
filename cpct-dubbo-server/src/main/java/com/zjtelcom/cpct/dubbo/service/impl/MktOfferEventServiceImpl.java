package com.zjtelcom.cpct.dubbo.service.impl;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.MktOfferEventMapper;
import com.zjtelcom.cpct.domain.event.MktOfferEventDO;
import com.zjtelcom.cpct.dubbo.service.MktOfferEventService;
import com.zjtelcom.cpct.util.RedisUtils;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class MktOfferEventServiceImpl implements MktOfferEventService {
    private static final Logger log = LoggerFactory.getLogger(MktOfferEventServiceImpl.class);

    @Autowired
    private MktOfferEventMapper mktOfferEventMapper;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private RedisUtils redisUtils;
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
                    log.info("c：" + offerCode);
                    continue;
                };
                //否则查数据库

                //List<MktOfferEventDO> mktOfferEventDOList = mktOfferEventMapper.getEventIdByOfferNbr(offerCode,Integer.parseInt(eventType));
                Long initId = mktOfferEventMapper.selectInitIdByOfferNbr(offerCode);
                Long mktCamId = mktOfferEventMapper.selectMktIdByInitId(initId);
                List<MktOfferEventDO> mktOfferEventDOList = mktOfferEventMapper.getEventIdByCamId(mktCamId,Integer.parseInt(eventType));
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

                            if(mktOfferEventDO.getEventId()!=null){
                                Long eventId = mktOfferEventDO.getEventId();
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
