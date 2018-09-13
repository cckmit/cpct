package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtTypeMapper;
import com.zjtelcom.cpct.domain.event.EventTypeDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.dto.event.ContactEvtType;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.request.event.QryContactEvtTypeReq;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.event.ContactEvtTypeService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtTypeService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct_prd.dao.event.ContactEvtTypePrdMapper;
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
 * @Description:事件目录同步
 */
@Service
@Transactional
public class SynContactEvtTypeServiceImpl extends BaseService implements SynContactEvtTypeService{

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private ContactEvtTypeMapper contactEvtTypeMapper;
    @Autowired
    private ContactEvtTypePrdMapper contactEvtTypePrdMapper;

    //同步表名
    private static final String tableName="event_type";


    /**
     * 单个事件目录同步
     * @param eventId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleEventType(Long eventId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        ContactEvtType contactEvtType = contactEvtTypeMapper.selectByPrimaryKey(eventId);
        if(contactEvtType==null){
            throw new SystemException("对应事件目录不存在");
        }
        //同步时查看是新增还是更新
        ContactEvtType type = contactEvtTypePrdMapper.selectByPrimaryKey(eventId);
        if(type==null){
             contactEvtTypePrdMapper.createContactEvtType(contactEvtType);
             synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.add.getType());
        }else{
             contactEvtTypePrdMapper.modContactEvtType(contactEvtType);
             synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.update.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    /**
     * 批量事件目录同步
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchEventType(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查出准生产的所有事件目录
        List<ContactEvtType> prdList = contactEvtTypeMapper.qryContactEvtTypeList(new QryContactEvtTypeReq());
        //查出生产环境所有事件目录
        List<ContactEvtType> realList = contactEvtTypePrdMapper.qryContactEvtTypeList(new QryContactEvtTypeReq());

        List<ContactEvtType> addList=new ArrayList<ContactEvtType>();
        List<ContactEvtType> updateList=new ArrayList<ContactEvtType>();
        List<ContactEvtType> deleteList=new ArrayList<ContactEvtType>();
        for(ContactEvtType c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getEvtTypeId()-realList.get(i).getEvtTypeId()==0){
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
        for(ContactEvtType c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getEvtTypeId()-prdList.get(i).getEvtTypeId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(ContactEvtType c:addList){
            contactEvtTypePrdMapper.createContactEvtType(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtTypeId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(ContactEvtType c:updateList){
            contactEvtTypePrdMapper.modContactEvtType(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtTypeId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(ContactEvtType c:deleteList){
            contactEvtTypePrdMapper.deleteByPrimaryKey(c.getEvtTypeId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtTypeId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }
}
