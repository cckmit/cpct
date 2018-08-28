package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.EventSorceMapper;
import com.zjtelcom.cpct.domain.event.EventSorceDO;
import com.zjtelcom.cpct.dto.event.ContactEvt;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.SynEventSorceService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct_prd.dao.event.EventSorcePrdMapper;
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
 * @Description:事件源同步
 */
@Service
@Transactional
public class SynEventSorceServiceImpl extends BaseService implements SynEventSorceService{


    @Autowired
    private EventSorceMapper eventSorceMapper;
    @Autowired
    private EventSorcePrdMapper eventSorcePrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;

    //同步表名
    public static final String tableName="event_sorce";

    /**
     * 单个事件源同步
     * @param eventId
     * @param roleName
     * @return
     */
    @Transactional(value="prodTransactionManager")
    @Override
    public Map<String, Object> synchronizeSingleEventSorce(Long eventId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        try {
        EventSorceDO eventSorceDO = eventSorceMapper.selectByPrimaryKey(eventId);
        if(eventSorceDO==null){
            throw new SystemException("对应事件源不存在");
        }
        EventSorceDO eventSorceDO1 = eventSorcePrdMapper.selectByPrimaryKey(eventId);
        if(eventSorceDO1==null){
            eventSorcePrdMapper.insert(eventSorceDO);
            synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.add.getType());
        }else{
            eventSorcePrdMapper.updateByPrimaryKey(eventSorceDO);
            synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.update.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
    } catch (Exception e) {
        maps.put("resultCode", CommonConstant.CODE_FAIL);
        maps.put("resultMsg", e.getMessage());
        logger.error("[op:SynEventSorceServiceImpl] 通过主键同步单个事件源 "+tableName+"失败！Exception: ", eventId, e);
    }
        return maps;
    }

    /**
     * 批量数据源同步
     * @param roleName
     * @return
     */
    @Transactional(value="prodTransactionManager")
    @Override
    public Map<String, Object> synchronizeBatchEventSorce(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        try {
        //查看准生产环境所有事件源
        List<EventSorceDO> prdList = eventSorceMapper.selectAll();
        //查看生产环境所有事件源
        List<EventSorceDO> realList = eventSorcePrdMapper.selectAll();

        List<EventSorceDO> addList=new ArrayList<EventSorceDO>();
        List<EventSorceDO> updateList=new ArrayList<EventSorceDO>();
        List<EventSorceDO> deleteList=new ArrayList<EventSorceDO>();

        for(EventSorceDO c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getEvtSrcId()-realList.get(i).getEvtSrcId()==0){
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
        for(EventSorceDO c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getEvtSrcId()-prdList.get(i).getEvtSrcId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(EventSorceDO c:addList){
            eventSorcePrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtSrcId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(EventSorceDO c:updateList){
            eventSorcePrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtSrcId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(EventSorceDO c:deleteList){
            eventSorcePrdMapper.deleteByPrimaryKey(c.getEvtSrcId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtSrcId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
    } catch (Exception e) {
        maps.put("resultCode", CommonConstant.CODE_FAIL);
        maps.put("resultMsg", e.getMessage());
        logger.error("[op:SynEventSorceServiceImpl] 批量同步数据源 "+tableName+"失败！Exception: ", e);
        }
        return maps;
    }
}
