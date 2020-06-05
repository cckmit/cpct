package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.EventRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.MktOfferEventMapper;
import com.zjtelcom.cpct.domain.channel.EventRel;
import com.zjtelcom.cpct.domain.event.MktOfferEventDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.service.channel.EventRelService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
                List<MktOfferEventDO> mktOfferEventDOList = mktOfferEventMapper.getEventIdByOfferNbr(Integer.parseInt(offerCode),Integer.parseInt(eventType));
                log.info(" 数据库返回：mktOfferEventDOList" + mktOfferEventDOList);
                HashMap<String,Object> dataMap = new HashMap<String,Object>();
                if(mktOfferEventDOList.size() == 0){
                    List<String> eventList = new ArrayList<>();
                    dataMap.put(offerCode, eventList);
                    data.add(dataMap);
                    continue;
                }else {
                    List<String> eventList = new ArrayList<>();
                    for (MktOfferEventDO mktOfferEventDO : mktOfferEventDOList){
                        String eventName = mktOfferEventDO.getEventName();
                        eventList.add(eventName);
                    }
                    dataMap.put(offerCode,eventList);
                    data.add(dataMap);
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
