package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulConditionMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulMapper;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import com.zjtelcom.cpct.domain.channel.EventItem;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.EventMatchRulCondition;
import com.zjtelcom.cpct.dto.event.EventMatchRulDTO;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.util.RedisUtils_prd;
import com.zjtelcom.cpct_prd.dao.campaign.MktCamEvtRelPrdMapper;
import com.zjtelcom.cpct_prd.dao.campaign.MktCampaignPrdMapper;
import com.zjtelcom.cpct_prd.dao.event.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/28
 * @Description:同步事件 需要同步关联的有：事件采集项   事件规则   事件规则条件   事件活动关联
 */
@Service
@Transactional
public class SynContactEvtServiceImpl extends BaseService implements SynContactEvtService {

    @Autowired
    private ContactEvtMapper contactEvtMapper;
    @Autowired
    private ContactEvtPrdMapper contactEvtPrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private ContactEvtItemMapper contactEvtItemMapper;
    @Autowired
    private ContactEvtItemPrdMapper contactEvtItemPrdMapper;
    @Autowired
    private EventMatchRulMapper eventMatchRulMapper;
    @Autowired
    private EventMatchRulPrdMapper eventMatchRulPrdMapper;
    @Autowired
    private EventMatchRulConditionMapper eventMatchRulConditionMapper;
    @Autowired
    private EventMatchRulConditionPrdMapper eventMatchRulConditionPrdMapper;
    @Autowired
    private MktCamEvtRelMapper mktCamEvtRelMapper;
    @Autowired
    private MktCamEvtRelPrdMapper mktCamEvtRelPrdMapper;
    @Autowired
    private MktCampaignPrdMapper mktCampaignPrdMapper;
    @Autowired
    private RedisUtils_prd redisUtils_prd;
    @Autowired
    private CamEvtRelPrdMapper camEvtRelPrdMapper;
    //同步表名
    private static final String tableName = "event";


    /**
     * 同步单个事件
     *
     * @param eventId  事件id
     * @param roleName 操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleEvent(Long eventId, String roleName) {
        System.out.println("同步单个事件： "+eventId);
        Map<String, Object> maps = new HashMap<>();
        //查询源数据库
        ContactEvt contactEvt = contactEvtMapper.getEventById(eventId);
        if (contactEvt == null) {
            throw new SystemException("对应事件不存在");
        }
        //1.1关联的事件采集项
        List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());

        //1.2关联的事件规则
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(eventId);
        //1.2.1事件规则条件信息
        List<EventMatchRulCondition> listEventMatchRulCondition = new ArrayList<>();
        if (eventMatchRulDTO != null) {
            listEventMatchRulCondition = eventMatchRulConditionMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
        }

        //1.3关联的活动  关联活动的时候要判断一下该活动在生产环境是否存在 如果不存在就不要同步  如果存在就更新
        List<MktCamEvtRel> mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());


        //同步时查看是新增还是更新  查找生产数据库
        ContactEvt eventById = contactEvtPrdMapper.getEventById(eventId);
        if (eventById == null) {
            //2.1新增事件信息
            contactEvtPrdMapper.createContactEvtJt(contactEvt);
            synchronizeRecordService.addRecord(roleName, tableName, eventId, SynchronizeType.add.getType());
            //2.2新增关联的事件采集项
            if (!contactEvtItems.isEmpty()) {
                for (EventItem c : contactEvtItems) {
                    contactEvtItemPrdMapper.insert(c);
                }
            }
            //2.3新增关联的事件规则
            if (eventMatchRulDTO != null) {
                eventMatchRulPrdMapper.createEventMatchRul(eventMatchRulDTO);
            }
            //2.4新增关联的事件规则条件信息
            if (!listEventMatchRulCondition.isEmpty()) {
                for (EventMatchRulCondition e : listEventMatchRulCondition) {
                    eventMatchRulConditionPrdMapper.insertEventMatchRulCondition(e);
                }
            }
            //2.5新增关联的活动   只能新增生产环境存在的活动
            if (!mktCamEvtRels.isEmpty()) {
                List<MktCampaignDO> mktCampaignDOS = mktCampaignPrdMapper.selectAll();
                if(!mktCampaignDOS.isEmpty()){
                    for (MktCamEvtRel mktCamEvtRel:mktCamEvtRels){
                        for (MktCampaignDO m : mktCampaignDOS) {
                            if(m.getMktCampaignId()-mktCamEvtRel.getMktCampaignId()==0){
                                camEvtRelPrdMapper.insert(mktCamEvtRel);
                                break;
                            }

                        }
                    }
                }
            }
        } else {
            //3.1修改  事件关联的采集项,规则条件,关联活动都有可能新增或者修改或者删除
            contactEvtPrdMapper.modContactEvtJt(contactEvt);
            synchronizeRecordService.addRecord(roleName, tableName, eventId, SynchronizeType.update.getType());
            //3.2修改关联的事件采集项
            diffEventItem(contactEvtItems, eventById);
            //3.3修改关联的事件规则
            if (eventMatchRulDTO != null) {
                eventMatchRulPrdMapper.updateByPrimaryKey(eventMatchRulDTO);
            }
            //3.4修改关联的事件规则条件信息
            diffEventMatchRulCondition(listEventMatchRulCondition, eventById);
            //3.5修改关联的活动
            diffMktCamEvtRel(mktCamEvtRels, eventById);
            // 删除事件接入的标签缓存
            redisUtils_prd.del("EVT_ALL_LABEL_" + eventId);

        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }


    /**
     * 3.2比较事件关联的事件采集项
     *
     * @param prdList
     * @param contactEvt
     */
    public void diffEventItem(List<EventItem> prdList, ContactEvt contactEvt) {
        //得到生产环境的事件采集项
        List<EventItem> realList = contactEvtItemPrdMapper.listEventItem(contactEvt.getContactEvtId());
        //判断哪些需要新增  修改  或者删除
        //三个集合分别表示需要 新增的   修改的    删除的
        List<EventItem> addList = new ArrayList<EventItem>();
        List<EventItem> updateList = new ArrayList<EventItem>();
        List<EventItem> deleteList = new ArrayList<EventItem>();
        //首先判断准生产 或生产是否存在某一方数据修改为0的情况
        if (prdList.isEmpty() || realList.isEmpty()) {
            if (prdList.isEmpty() && !realList.isEmpty()) {
                //清除生产环境数据
                for (int i = 0; i < realList.size(); i++) {
                    contactEvtItemPrdMapper.deleteByPrimaryKey(realList.get(i).getEvtItemId());
                }
            } else if (!prdList.isEmpty() && realList.isEmpty()) {
                //全量新增准生产的数据到生产环境
                for (int i = 0; i < prdList.size(); i++) {
                    contactEvtItemPrdMapper.insert(prdList.get(i));
                }
            }
            return;
        }

        for (EventItem c : prdList) {
            for (int i = 0; i < realList.size(); i++) {
                if (c.getEvtItemId() - realList.get(i).getEvtItemId() == 0) {
                    //需要修改的
                    updateList.add(c);
                    break;
                } else if (i == realList.size() - 1) {
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for (EventItem c : realList) {
            for (int i = 0; i < prdList.size(); i++) {
                if (c.getEvtItemId() - prdList.get(i).getEvtItemId() == 0) {
                    break;
                } else if (i == prdList.size() - 1) {
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for (EventItem c : addList) {
            contactEvtItemPrdMapper.insert(c);
        }
        //开始修改
        for (EventItem c : updateList) {
            contactEvtItemPrdMapper.updateByPrimaryKey(c);
        }
        //开始删除
        for (EventItem c : deleteList) {
            contactEvtItemPrdMapper.deleteByPrimaryKey(c.getEvtItemId());
        }
    }


    /**
     * 3.4事件关联的事件规则条件信息比较
     *
     * @param prdList    准生产事件规则条件集合
     * @param contactEvt 事件
     */
    public void diffEventMatchRulCondition(List<EventMatchRulCondition> prdList, ContactEvt contactEvt) {
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulPrdMapper.listEventMatchRul(contactEvt.getContactEvtId());
        //1.2.1事件规则条件信息
        List<EventMatchRulCondition> realList = eventMatchRulConditionPrdMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
        //开始比较
        List<EventMatchRulCondition> addList = new ArrayList<EventMatchRulCondition>();
        List<EventMatchRulCondition> updateList = new ArrayList<EventMatchRulCondition>();
        List<EventMatchRulCondition> deleteList = new ArrayList<EventMatchRulCondition>();
        //首先判断准生产 或生产是否存在某一方数据修改为0的情况
        if (prdList.isEmpty() || realList.isEmpty()) {
            if (prdList.isEmpty() && !realList.isEmpty()) {
                //清除生产环境数据
                for (int i = 0; i < realList.size(); i++) {
                    eventMatchRulConditionPrdMapper.delEventMatchRulCondition(realList.get(i));
                }
            } else if (!prdList.isEmpty() && realList.isEmpty()) {
                //全量新增准生产的数据到生产环境
                for (int i = 0; i < prdList.size(); i++) {
                    eventMatchRulConditionPrdMapper.insertEventMatchRulCondition(prdList.get(i));
                }
            }
            return;
        }

        for (EventMatchRulCondition c : prdList) {
            for (int i = 0; i < realList.size(); i++) {
                if (c.getConditionId() - realList.get(i).getConditionId() == 0) {
                    //需要修改的
                    updateList.add(c);
                    break;
                } else if (i == realList.size() - 1) {
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for (EventMatchRulCondition c : realList) {
            for (int i = 0; i < prdList.size(); i++) {
                if (c.getConditionId() - prdList.get(i).getConditionId() == 0) {
                    break;
                } else if (i == prdList.size() - 1) {
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for (EventMatchRulCondition c : addList) {
            eventMatchRulConditionPrdMapper.insertEventMatchRulCondition(c);
        }
        //开始修改
        for (EventMatchRulCondition c : updateList) {
            eventMatchRulConditionPrdMapper.updateByPrimaryKey(c);
        }
        //开始删除
        for (EventMatchRulCondition c : deleteList) {
            eventMatchRulConditionPrdMapper.delEventMatchRulCondition(c);
        }
    }


    /**
     * 事件关联的活动同步
     *
     * @param prdList   准生产事件关联的活动
     * @param contactEvt
     */
    public void diffMktCamEvtRel(List<MktCamEvtRel> prdList, ContactEvt contactEvt) {

        List<MktCamEvtRel> realList = camEvtRelPrdMapper.qryBycontactEvtId(contactEvt.getContactEvtId());
        //开始比较
        List<MktCamEvtRel> addList = new ArrayList<MktCamEvtRel>();
        List<MktCamEvtRel> updateList = new ArrayList<MktCamEvtRel>();
        List<MktCamEvtRel> deleteList = new ArrayList<MktCamEvtRel>();
        //首先判断准生产 或生产是否存在某一方数据修改为0的情况
        if (prdList.isEmpty() || realList.isEmpty()) {
            if (prdList.isEmpty() && !realList.isEmpty()) {
                //清除生产环境数据
                for (int i = 0; i < realList.size(); i++) {
                    camEvtRelPrdMapper.deleteByMktCampaignId(realList.get(i).getMktCampaignId());
                }
            } else if (!prdList.isEmpty() && realList.isEmpty()) {
                //新增准生产的数据到生产环境  只能同步准生产和生产都存在的活动
                List<MktCampaignDO> mktCampaignDOS = mktCampaignPrdMapper.selectAll();
                if(!mktCampaignDOS.isEmpty()){
                    for (MktCamEvtRel mktCamEvtRel:prdList){
                        for (MktCampaignDO m : mktCampaignDOS) {
                            if(m.getMktCampaignId()-mktCamEvtRel.getMktCampaignId()==0){
                                camEvtRelPrdMapper.insert(mktCamEvtRel);
                                break;
                            }

                        }
                    }
                }

            }
            return;
        }

        for (MktCamEvtRel c : prdList) {
            for (int i = 0; i < realList.size(); i++) {
                if (c.getMktCampaignId() - realList.get(i).getMktCampaignId() == 0) {
                    //需要修改的
                    updateList.add(c);
                    break;
                } else if (i == realList.size() - 1) {
                    //需要新增的  准生产存在，生产不存在
                    List<MktCampaignDO> mktCampaignDOS = mktCampaignPrdMapper.selectAll();
                    if(!mktCampaignDOS.isEmpty()){
                            for (MktCampaignDO m : mktCampaignDOS) {
                                if(m.getMktCampaignId()-c.getMktCampaignId()==0){
                                    addList.add(c);
                                    break;
                                }
                            }
                    }

                }
            }
        }

        for (MktCamEvtRel c : realList) {
            for (int i = 0; i < prdList.size(); i++) {
                if (c.getMktCampaignId() - prdList.get(i).getMktCampaignId() == 0) {
                    break;
                } else if (i == prdList.size() - 1) {
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for (MktCamEvtRel c : addList) {
            camEvtRelPrdMapper.insert(c);
        }
        //开始修改
        for (MktCamEvtRel c : updateList) {
            camEvtRelPrdMapper.updateByPrimaryKey(c);
        }
        //开始删除 根据关联关系的主键删除
        for (MktCamEvtRel c : deleteList) {
            camEvtRelPrdMapper.deleteByPrimaryKey(c.getMktCampEvtRelId());
        }
    }


    /**
     * 批量事件同步 生产环境不存在的就新增，存在的则修改更新
     * 生产环境存在，准生产环境不存在的同步时就删除生产环境对应事件
     *
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchEvent(String roleName) {
        Map<String, Object> maps = new HashMap<>();
        //先查出准生产的所有事件
        List<ContactEvt> prdList = contactEvtMapper.query();
        //查出生产的所有事件
        List<ContactEvt> realList = contactEvtPrdMapper.query();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<ContactEvt> addList = new ArrayList<ContactEvt>();
        List<ContactEvt> updateList = new ArrayList<ContactEvt>();
        List<ContactEvt> deleteList = new ArrayList<ContactEvt>();
        for (ContactEvt c : prdList) {
            for (int i = 0; i < realList.size(); i++) {
                if (c.getContactEvtId() - realList.get(i).getContactEvtId() == 0) {
                    //需要修改的
                    updateList.add(c);
                    break;
                } else if (i == realList.size() - 1) {
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        //查出需要删除的事件
        for (ContactEvt c : realList) {
            for (int i = 0; i < prdList.size(); i++) {
                if (c.getContactEvtId() - prdList.get(i).getContactEvtId() == 0) {
                    break;
                } else if (i == prdList.size() - 1) {
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for (ContactEvt c : addList) {
            addEvent(c);
            synchronizeRecordService.addRecord(roleName, tableName, c.getContactEvtId(), SynchronizeType.add.getType());
            // 删除事件接入的标签缓存
            redisUtils_prd.del("EVT_ALL_LABEL_" + c.getContactEvtId());
        }
        //开始修改
        for (ContactEvt c : updateList) {
            updateEvent(c);
            synchronizeRecordService.addRecord(roleName, tableName, c.getContactEvtId(), SynchronizeType.update.getType());
            // 删除事件接入的标签缓存
            redisUtils_prd.del("EVT_ALL_LABEL_" + c.getContactEvtId());
        }
        //开始删除
        for (ContactEvt c : deleteList) {
            deleteSingleEvent(c.getContactEvtId(), roleName);
            synchronizeRecordService.addRecord(roleName, tableName, c.getContactEvtId(), SynchronizeType.delete.getType());
            // 删除事件接入的标签缓存
            redisUtils_prd.del("EVT_ALL_LABEL_" + c.getContactEvtId());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    /**
     * 删除事件 事件有关联活动时 该事件不能删除
     *
     * @param eventId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> deleteSingleEvent(Long eventId, String roleName) {
        System.out.println("同步删除事件");
        Map<String, Object> maps = new HashMap<>();
        ContactEvt contactEvt = contactEvtPrdMapper.getEventById(eventId);
        if (contactEvt != null) {
            List<MktCamEvtRel> mktCamEvtRels = camEvtRelPrdMapper.qryBycontactEvtId(contactEvt.getContactEvtId());
            if (mktCamEvtRels.isEmpty()) {
                //没有关联活动 直接删除事件
                contactEvtPrdMapper.delEvent(eventId);
                contactEvtItemPrdMapper.deleteByEventId(eventId);
                EventMatchRulDTO eventMatchRulDTO = eventMatchRulPrdMapper.listEventMatchRul(eventId);
                if(eventMatchRulDTO != null) {
                    eventMatchRulPrdMapper.delEventMatchRul(eventMatchRulDTO);
                    List<EventMatchRulCondition> eventMatchRulConditionList = eventMatchRulConditionPrdMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
                    for(EventMatchRulCondition eventMatchRulCondition : eventMatchRulConditionList) {
                        eventMatchRulConditionPrdMapper.delEventMatchRulCondition(eventMatchRulCondition);
                    }
                }
            } else {
                throw new SystemException(contactEvt.getContactEvtCode() + "该事件有关联活动信息，不能删除");
            }
        }
        synchronizeRecordService.addRecord(roleName, tableName, eventId, SynchronizeType.delete.getType());
        // 删除事件接入的标签缓存
        redisUtils_prd.del("EVT_ALL_LABEL_" + eventId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }


    /**
     * 新增单个事件  同时新增其关联的 事件采集项   事件规则   事件规则条件   事件活动关联
     *
     * @param contactEvt
     */
    public void addEvent(ContactEvt contactEvt) {
        //1.1关联的事件采集项
        List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());

        //1.2关联的事件规则
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(contactEvt.getExtEventId());
        //1.2.1事件规则条件信息
        List<EventMatchRulCondition> listEventMatchRulCondition = new ArrayList<>();
        if (eventMatchRulDTO != null) {
            listEventMatchRulCondition = eventMatchRulConditionMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
        }
        //1.3关联的活动
        List<MktCamEvtRel> mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());

        //2.1新增事件信息
        contactEvtPrdMapper.createContactEvtJt(contactEvt);
        //2.2新增关联的事件采集项
        if (!contactEvtItems.isEmpty()) {
            for (EventItem c : contactEvtItems) {
                contactEvtItemPrdMapper.insert(c);
            }
        }
        //2.3新增关联的事件规则
        if (eventMatchRulDTO != null) {
            eventMatchRulPrdMapper.createEventMatchRul(eventMatchRulDTO);
        }
        //2.4新增关联的事件规则条件信息
        if (!listEventMatchRulCondition.isEmpty()) {
            for (EventMatchRulCondition e : listEventMatchRulCondition) {
                eventMatchRulConditionPrdMapper.insertEventMatchRulCondition(e);
            }
        }
        //2.5新增关联的活动
        if (!mktCamEvtRels.isEmpty()) {
            List<MktCampaignDO> mktCampaignDOS = mktCampaignPrdMapper.selectAll();
            if(!mktCampaignDOS.isEmpty()){
                for (MktCamEvtRel mktCamEvtRel:mktCamEvtRels){
                    for (MktCampaignDO m : mktCampaignDOS) {
                        if(m.getMktCampaignId()-mktCamEvtRel.getMktCampaignId()==0){
                            camEvtRelPrdMapper.insert(mktCamEvtRel);
                            break;
                        }

                    }
                }
            }
        }

    }


    /**
     * 修改单个事件  同时修改其关联的 事件采集项   事件规则   事件规则条件   事件活动关联
     *
     * @param contactEvt
     */
    public void updateEvent(ContactEvt contactEvt) {
        //1.1关联的事件采集项
        List<EventItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());

        //1.2关联的事件规则
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(contactEvt.getExtEventId());
        //1.2.1事件规则条件信息
        List<EventMatchRulCondition> listEventMatchRulCondition = new ArrayList<>();
        if (eventMatchRulDTO != null) {
            listEventMatchRulCondition = eventMatchRulConditionMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
        }
        //1.3关联的活动
        List<MktCamEvtRel> mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());

        //3.1修改
        contactEvtPrdMapper.modContactEvtJt(contactEvt);
        //3.2修改关联的事件采集项
        if (!contactEvtItems.isEmpty()) {
            diffEventItem(contactEvtItems, contactEvt);
        }
        //3.3修改关联的事件规则
        if (eventMatchRulDTO != null) {
            eventMatchRulPrdMapper.updateByPrimaryKey(eventMatchRulDTO);
        }
        //3.4修改关联的事件规则条件信息
        if (!listEventMatchRulCondition.isEmpty()) {
            diffEventMatchRulCondition(listEventMatchRulCondition, contactEvt);
        }
        //3.5修改关联的活动
        if (!mktCamEvtRels.isEmpty()) {
            diffMktCamEvtRel(mktCamEvtRels, contactEvt);
        }
    }


}
