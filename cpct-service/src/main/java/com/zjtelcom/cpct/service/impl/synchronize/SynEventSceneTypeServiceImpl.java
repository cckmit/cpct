package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.EventSceneTypeMapper;
import com.zjtelcom.cpct.domain.event.EventSceneTypeDO;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynEventSceneTypeService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct_prd.dao.event.EventSceneTypePrdMapper;
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
 * @Date: 2018/9/13
 * @Description:事件场景目录同步
 */
@Service
@Transactional
public class SynEventSceneTypeServiceImpl implements SynEventSceneTypeService {

    @Autowired
    private EventSceneTypeMapper eventSceneTypeMappper;
    @Autowired
    private EventSceneTypePrdMapper eventSceneTypePrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;


    //同步表名
    private static final String tableName = "event_scene_type";


    /**
     * 单个事件场景目录同步
     * @param eventSceneTypeId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleEventSceneType(Long eventSceneTypeId, String roleName) {
        Map<String, Object> maps = new HashMap<>();
        EventSceneTypeDO eventSceneTypeDO = eventSceneTypeMappper.selectByPrimaryKey(eventSceneTypeId);
        if(null==eventSceneTypeDO){
            throw new SystemException("对应事件场景目录不存在");
        }
        EventSceneTypeDO eventSceneTypeDO1 = eventSceneTypePrdMapper.selectByPrimaryKey(eventSceneTypeId);
        if(null==eventSceneTypeDO1){
              eventSceneTypePrdMapper.insert(eventSceneTypeDO);
              synchronizeRecordService.addRecord(roleName,tableName,eventSceneTypeId, SynchronizeType.add.getType());
        }else{
            eventSceneTypePrdMapper.updateByPrimaryKey(eventSceneTypeDO);
            synchronizeRecordService.addRecord(roleName,tableName,eventSceneTypeId, SynchronizeType.update.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }


    /**
     * 事件场景目录批量同步
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchEventSceneType(String roleName) {
        Map<String, Object> maps = new HashMap<>();

        //查出准生产的所有事件场景目录
        List<EventSceneTypeDO> prdList = eventSceneTypeMappper.selectAll();
        //查出生产环境所有事件场景目录
        List<EventSceneTypeDO> realList = eventSceneTypePrdMapper.selectAll();

        List<EventSceneTypeDO> addList=new ArrayList<EventSceneTypeDO>();
        List<EventSceneTypeDO> updateList=new ArrayList<EventSceneTypeDO>();
        List<EventSceneTypeDO> deleteList=new ArrayList<EventSceneTypeDO>();
        for(EventSceneTypeDO c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getEvtSceneTypeId()-realList.get(i).getEvtSceneTypeId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        //查出需要删除的事件场景目录
        for(EventSceneTypeDO c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getEvtSceneTypeId()-prdList.get(i).getEvtSceneTypeId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(EventSceneTypeDO c:addList){
            eventSceneTypePrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtSceneTypeId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(EventSceneTypeDO c:updateList){
            eventSceneTypePrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtSceneTypeId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(EventSceneTypeDO c:deleteList){
            eventSceneTypePrdMapper.deleteByPrimaryKey(c.getEvtSceneTypeId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getEvtSceneTypeId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }
}
