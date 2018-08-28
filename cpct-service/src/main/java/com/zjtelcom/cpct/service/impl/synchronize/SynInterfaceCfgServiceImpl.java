package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.event.InterfaceCfgMapper;
import com.zjtelcom.cpct.domain.event.EventSorceDO;
import com.zjtelcom.cpct.domain.event.InterfaceCfg;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.synchronize.SynInterfaceCfgService;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct_prd.dao.event.InterfaceCfgPrdMapper;
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
 * @Description:事件源接口同步
 */
@Service
@Transactional
public class SynInterfaceCfgServiceImpl extends BaseService implements SynInterfaceCfgService{

    @Autowired
    private InterfaceCfgPrdMapper interfaceCfgPrdMapper;
    @Autowired
    private InterfaceCfgMapper interfaceCfgMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;

    //同步表名
    public static final String tableName="interface_cfg";

    /**
     * 同步单个数据源接口
     * @param eventId
     * @param roleName
     * @return
     */
    @Transactional(value="prodTransactionManager")
    @Override
    public Map<String, Object> synchronizeSingleEventInterface(Long eventId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        try {
        InterfaceCfg interfaceCfg = interfaceCfgMapper.selectByPrimaryKey(eventId);
        if(interfaceCfg==null){
            throw new SystemException("对应事件源接口不存在");
        }
        InterfaceCfg interfaceCfg1 = interfaceCfgPrdMapper.selectByPrimaryKey(eventId);
        if(interfaceCfg1==null){
            interfaceCfgPrdMapper.insert(interfaceCfg);
            synchronizeRecordService.addRecord(roleName,tableName,eventId, SynchronizeType.add.getType());
        }else{
            interfaceCfgPrdMapper.updateByPrimaryKey(interfaceCfg);
            synchronizeRecordService.addRecord(roleName,tableName,eventId,SynchronizeType.update.getType());
        }


        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
    } catch (Exception e) {
        maps.put("resultCode", CommonConstant.CODE_FAIL);
        maps.put("resultMsg", e.getMessage());
        logger.error("[op:SynInterfaceCfgServiceImpl] 通过主键同步单个事件源接口 "+tableName+"失败！Exception: ", eventId, e);
    }
        return maps;
    }


    /**
     * 事件源接口批量同步
     * @param roleName
     * @return
     */
    @Transactional(value="prodTransactionManager")
    @Override
    public Map<String, Object> synchronizeBatchEventInterface(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        try {
        List<InterfaceCfg> prdList = interfaceCfgMapper.selectAll();
        List<InterfaceCfg> realList = interfaceCfgPrdMapper.selectAll();

        List<InterfaceCfg> addList=new ArrayList<InterfaceCfg>();
        List<InterfaceCfg> updateList=new ArrayList<InterfaceCfg>();
        List<InterfaceCfg> deleteList=new ArrayList<InterfaceCfg>();

        for(InterfaceCfg c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getInterfaceCfgId()-realList.get(i).getInterfaceCfgId()==0){
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
        for(InterfaceCfg c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getInterfaceCfgId()-prdList.get(i).getInterfaceCfgId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(InterfaceCfg c:addList){
            interfaceCfgPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getInterfaceCfgId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(InterfaceCfg c:updateList){
            interfaceCfgPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getInterfaceCfgId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(InterfaceCfg c:deleteList){
            interfaceCfgPrdMapper.deleteByPrimaryKey(c.getInterfaceCfgId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getInterfaceCfgId(), SynchronizeType.delete.getType());
        }


        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
    } catch (Exception e) {
        maps.put("resultCode", CommonConstant.CODE_FAIL);
        maps.put("resultMsg", e.getMessage());
        logger.error("[op:SynInterfaceCfgServiceImpl] 批量同步事件源接口 "+tableName+"失败！Exception: ", e);
        }
        return maps;
    }
}
