package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.EventSceneMapper;
import com.zjtelcom.cpct.dto.event.EventScene;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.request.event.QryEventSceneListReq;
import com.zjtelcom.cpct.service.synchronize.SynEventSceneService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct_prd.dao.event.EventScenePrdMapper;
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
 * @Description: 事件场景同步
 */
@Service
@Transactional
public class SynEventSceneServiceImpl implements SynEventSceneService {

    @Autowired
    private EventSceneMapper eventSceneMapper;
    @Autowired
    private EventScenePrdMapper eventScenePrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;

    //同步表名
    private static final String tableName="event_scene";
    /**
     * 单个事件场景同步
     * @param eventSceneId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleEventScene(Long eventSceneId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        EventScene eventScene = eventSceneMapper.selectByPrimaryKey(eventSceneId);

        if(null==eventScene){
            throw new SystemException("对应事件场景不存在");
        }
        eventScene.setEventSceneDesc("第一数据源第一数据源");
        eventSceneMapper.updateById(eventScene);
        EventScene eventScene1 = eventScenePrdMapper.selectByPrimaryKey(eventSceneId);
        if(null==eventScene1){
            eventScenePrdMapper.createEventScene(eventScene);
            synchronizeRecordService.addRecord(roleName,tableName,eventSceneId, SynchronizeType.add.getType());
        }else{
            eventScenePrdMapper.updateById(eventScene);
            synchronizeRecordService.addRecord(roleName,tableName,eventSceneId, SynchronizeType.update.getType());
        }
        System.out.println(1/0);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }





    /**
     * 批量事件场景同步
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchEventScene(String roleName) {
        Map<String,Object> maps = new HashMap<>();

        //查出准生产的所有事件场景
        List<EventScene> prdList = eventSceneMapper.qryEventSceneList(new QryEventSceneListReq());
        //查出生产环境所有事件场景
        List<EventScene> realList = eventScenePrdMapper.qryEventSceneList(new QryEventSceneListReq());

        List<EventScene> addList=new ArrayList<EventScene>();
        List<EventScene> updateList=new ArrayList<EventScene>();
        List<EventScene> deleteList=new ArrayList<EventScene>();
        for(EventScene c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getEventSceneId()-realList.get(i).getEventSceneId()==0){
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
        for(EventScene c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getEventSceneId()-prdList.get(i).getEventSceneId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(EventScene c:addList){
            eventScenePrdMapper.createEventScene(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEventSceneId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(EventScene c:updateList){
            eventScenePrdMapper.updateById(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getEventSceneId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(EventScene c:deleteList){
            eventScenePrdMapper.deleteByPrimaryKey(c.getEventSceneId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getEventSceneId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }




}





