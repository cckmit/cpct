package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.ContactEvtMapper;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.SynContactEvtService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct_prd.dao.event.ContactEvtPrdMapper;
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
 * @Description:同步事件
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





    /**
     * 同步单个事件
     * @param eventId    事件id
     * @param roleName   操作人身份
     * @return
     */
    @Transactional(value="devTransactionManager")
    @Override
    public Map<String, Object> synchronizeSingleEvent(Long eventId, String roleName) {
        String tableName="event";   //同步表名称
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        ContactEvt contactEvt=contactEvtMapper.getEventById(eventId);
        contactEvt.setContactEvtDesc("测试第一数据源");
        contactEvtMapper.modContactEvtJt(contactEvt);

        //同步时查看是新增还是更新
        ContactEvt eventById = contactEvtPrdMapper.getEventById(eventId);
        if(eventById==null){
            contactEvtPrdMapper.createContactEvtJt(eventById);
        }else{
            eventById.setContactEvtDesc("测试第二数据源");
            contactEvtPrdMapper.modContactEvtJt(eventById);
        }

        if(eventId!=null){
            throw new RuntimeException("我故意抛出的");
        }
        //同步完成  新增一条同步记录
        synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.add.getType());

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
    @Transactional(value="devTransactionManager")
    @Override
    public Map<String, Object> synchronizeBatchEvent(String roleName) {
        String tableName="event";   //同步表名称
        Map<String,Object> maps = new HashMap<>();
        //先查出准生产的所有事件
        List<ContactEvt> contactEvts = contactEvtMapper.query();
        logger.info("准生产事件总数："+contactEvts.size());
        //查出生产的所有事件
        List<ContactEvt> prdContactEvts = contactEvtPrdMapper.query();
        logger.info("生产事件总数："+prdContactEvts.size());
        //三个集合分别表示需要 新增的   修改的    删除的
        List<ContactEvt> addList=new ArrayList<ContactEvt>();
        List<ContactEvt> updateList=new ArrayList<ContactEvt>();
        List<ContactEvt> deleteList=new ArrayList<ContactEvt>();
        for(ContactEvt c:contactEvts){
            for (int i = 0; i <prdContactEvts.size() ; i++) {
                if(c.getContactEvtId()-prdContactEvts.get(i).getContactEvtId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==prdContactEvts.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        //查出需要删除的事件
        for(ContactEvt c:prdContactEvts){
            for (int i = 0; i <contactEvts.size() ; i++) {
                if(c.getContactEvtId()-contactEvts.get(i).getContactEvtId()==0){
                    break;
                }else if (i==contactEvts.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        System.out.println(addList.size()+"  "+updateList.size()+"  "+deleteList.size());
        //开始新增
        for(ContactEvt c:addList){
            contactEvtPrdMapper.createContactEvtJt(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactEvtId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(ContactEvt c:updateList){
            contactEvtPrdMapper.modContactEvtJt(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactEvtId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(ContactEvt c:deleteList){
            contactEvtPrdMapper.delEvent(c.getContactEvtId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getContactEvtId(), SynchronizeType.delete.getType());
        }



        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

}
