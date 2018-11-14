package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.campaign.MktCamEvtRelMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtItemMapper;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulConditionMapper;
import com.zjtelcom.cpct.dao.event.EventMatchRulMapper;
import com.zjtelcom.cpct.dto.campaign.MktCamEvtRel;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtItem;
import com.zjtelcom.cpct.dto.event.EventMatchRulCondition;
import com.zjtelcom.cpct.dto.event.EventMatchRulDTO;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
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
 * @Description:同步事件  需要同步关联的有：事件采集项   事件规则   事件规则条件   事件活动关联
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
    private CamEvtRelPrdMapper camEvtRelPrdMapper;
    //同步表名
    private static final String tableName="event";



    /**
     * 同步单个事件
     * @param eventId    事件id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleEvent(Long eventId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        ContactEvt contactEvt=contactEvtMapper.getEventById(eventId);
        if(contactEvt==null){
            throw new SystemException("对应事件不存在");
        }
        //1.1关联的事件采集项
        List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());

        //1.2关联的事件规则
        EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(eventId);
          //1.2.1事件规则条件信息
          List<EventMatchRulCondition> listEventMatchRulCondition=new ArrayList<>();
          if(eventMatchRulDTO!=null){
             listEventMatchRulCondition = eventMatchRulConditionMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
          }

        //1.3关联的活动
        List<MktCamEvtRel> mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());


        //同步时查看是新增还是更新  查找生产数据库
        ContactEvt eventById = contactEvtPrdMapper.getEventById(eventId);
        if(eventById==null){
            //2.1新增事件信息
            contactEvtPrdMapper.createContactEvtJt(contactEvt);
            synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.add.getType());
            //2.2新增关联的事件采集项
            if(!contactEvtItems.isEmpty()){
                for (ContactEvtItem c:contactEvtItems){
                    contactEvtItemPrdMapper.insert(c);
                }
            }
            //2.3新增关联的事件规则
            if(eventMatchRulDTO!=null){
                eventMatchRulPrdMapper.createEventMatchRul(eventMatchRulDTO);
            }
            //2.4新增关联的事件规则条件信息
            if(!listEventMatchRulCondition.isEmpty()){
                 for (EventMatchRulCondition e:listEventMatchRulCondition){
                     eventMatchRulConditionPrdMapper.insertEventMatchRulCondition(e);
                 }
            }
            //2.5新增关联的活动
            if(!mktCamEvtRels.isEmpty()){
                 for (MktCamEvtRel m:mktCamEvtRels){
                     camEvtRelPrdMapper.insert(m);
                 }
            }
        }else{
            //3.1修改
            contactEvtPrdMapper.modContactEvtJt(contactEvt);
            synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.update.getType());
            //3.2修改关联的事件采集项
            if(!contactEvtItems.isEmpty()){
                for (ContactEvtItem c:contactEvtItems){
                    contactEvtItemPrdMapper.updateByPrimaryKey(c);
                }
            }
            //3.3修改关联的事件规则
            if(eventMatchRulDTO!=null){
                eventMatchRulPrdMapper.updateByPrimaryKey(eventMatchRulDTO);
            }
            //3.4修改关联的事件规则条件信息
            if(!listEventMatchRulCondition.isEmpty()){
                for (EventMatchRulCondition e:listEventMatchRulCondition){
                    eventMatchRulConditionPrdMapper.updateByPrimaryKey(e);
                }
            }
            //3.5修改关联的活动
            if(!mktCamEvtRels.isEmpty()){
                for (MktCamEvtRel m:mktCamEvtRels){
                    camEvtRelPrdMapper.updateByPrimaryKey(m);
                }
            }
        }
            maps.put("resultCode", CommonConstant.CODE_SUCCESS);
            maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量事件同步 生产环境不存在的就新增，存在的则修改更新
     *            生产环境存在，准生产环境不存在的同步时就删除生产环境对应事件
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchEvent(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //先查出准生产的所有事件
        List<ContactEvt> prdList = contactEvtMapper.query();
        //查出生产的所有事件
        List<ContactEvt> realList = contactEvtPrdMapper.query();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<ContactEvt> addList=new ArrayList<ContactEvt>();
        List<ContactEvt> updateList=new ArrayList<ContactEvt>();
        List<ContactEvt> deleteList=new ArrayList<ContactEvt>();
        for(ContactEvt c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getContactEvtId()-realList.get(i).getContactEvtId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        //查出需要删除的事件
        for(ContactEvt c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getContactEvtId()-prdList.get(i).getContactEvtId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(ContactEvt c:addList){
            addEvent(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactEvtId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(ContactEvt c:updateList){
            updateEvent(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactEvtId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(ContactEvt c:deleteList){
            deleteSingleEvent(c.getContactEvtId(),roleName);
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactEvtId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    /**
     * 删除事件 事件有关联活动时 该事件不能删除
     * @param eventId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> deleteSingleEvent(Long eventId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        ContactEvt contactEvt=contactEvtPrdMapper.getEventById(eventId);
        if(contactEvt!=null){
            List<MktCamEvtRel> mktCamEvtRels = camEvtRelPrdMapper.qryBycontactEvtId(contactEvt.getContactEvtId());
            if(mktCamEvtRels.isEmpty()){
                 //没有关联活动 直接删除事件
                contactEvtPrdMapper.delEvent(eventId);
            }else{
                throw new SystemException(contactEvt.getContactEvtCode()+"该事件有关联活动信息，不能删除");
            }
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }




    /**
     * 新增单个事件  同时新增其关联的 事件采集项   事件规则   事件规则条件   事件活动关联
     * @param contactEvt
     */
     public void addEvent(ContactEvt contactEvt){
         //1.1关联的事件采集项
         List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());

         //1.2关联的事件规则
         EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(contactEvt.getExtEventId());
         //1.2.1事件规则条件信息
         List<EventMatchRulCondition> listEventMatchRulCondition=new ArrayList<>();
         if(eventMatchRulDTO!=null){
             listEventMatchRulCondition = eventMatchRulConditionMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
         }
         //1.3关联的活动
         List<MktCamEvtRel> mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());

         //2.1新增事件信息
         contactEvtPrdMapper.createContactEvtJt(contactEvt);
         //2.2新增关联的事件采集项
         if(!contactEvtItems.isEmpty()){
             for (ContactEvtItem c:contactEvtItems){
                 contactEvtItemPrdMapper.insert(c);
             }
         }
         //2.3新增关联的事件规则
         if(eventMatchRulDTO!=null){
             eventMatchRulPrdMapper.createEventMatchRul(eventMatchRulDTO);
         }
         //2.4新增关联的事件规则条件信息
         if(!listEventMatchRulCondition.isEmpty()){
             for (EventMatchRulCondition e:listEventMatchRulCondition){
                 eventMatchRulConditionPrdMapper.insertEventMatchRulCondition(e);
             }
         }
         //2.5新增关联的活动
         if(!mktCamEvtRels.isEmpty()){
             for (MktCamEvtRel m:mktCamEvtRels){
                 camEvtRelPrdMapper.insert(m);
             }
         }

     }


    /**
     * 修改单个事件  同时修改其关联的 事件采集项   事件规则   事件规则条件   事件活动关联
     * @param contactEvt
     */
     public void updateEvent(ContactEvt contactEvt){
         //1.1关联的事件采集项
         List<ContactEvtItem> contactEvtItems = contactEvtItemMapper.listEventItem(contactEvt.getContactEvtId());

         //1.2关联的事件规则
         EventMatchRulDTO eventMatchRulDTO = eventMatchRulMapper.listEventMatchRul(contactEvt.getExtEventId());
         //1.2.1事件规则条件信息
         List<EventMatchRulCondition> listEventMatchRulCondition=new ArrayList<>();
         if(eventMatchRulDTO!=null){
             listEventMatchRulCondition = eventMatchRulConditionMapper.listEventMatchRulCondition(eventMatchRulDTO.getEvtMatchRulId());
         }
         //1.3关联的活动
         List<MktCamEvtRel> mktCamEvtRels = mktCamEvtRelMapper.qryBycontactEvtId(contactEvt.getContactEvtId());

         //3.1修改
         contactEvtPrdMapper.modContactEvtJt(contactEvt);
         //3.2修改关联的事件采集项
         if(!contactEvtItems.isEmpty()){
             for (ContactEvtItem c:contactEvtItems){
                 contactEvtItemPrdMapper.updateByPrimaryKey(c);
             }
         }
         //3.3修改关联的事件规则
         if(eventMatchRulDTO!=null){
             eventMatchRulPrdMapper.updateByPrimaryKey(eventMatchRulDTO);
         }
         //3.4修改关联的事件规则条件信息
         if(!listEventMatchRulCondition.isEmpty()){
             for (EventMatchRulCondition e:listEventMatchRulCondition){
                 eventMatchRulConditionPrdMapper.updateByPrimaryKey(e);
             }
         }
         //3.5修改关联的活动
         if(!mktCamEvtRels.isEmpty()){
             for (MktCamEvtRel m:mktCamEvtRels){
                 camEvtRelPrdMapper.updateByPrimaryKey(m);
             }
         }
     }


}
