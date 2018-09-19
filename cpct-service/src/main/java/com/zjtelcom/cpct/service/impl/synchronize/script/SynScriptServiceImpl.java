package com.zjtelcom.cpct.service.impl.synchronize.script;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.MktScriptMapper;
import com.zjtelcom.cpct.domain.channel.Script;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.script.SynScriptService;
import com.zjtelcom.cpct_prd.dao.script.MktScriptPrdMapper;
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
 * @Date: 2018/9/14
 * @Description:脚本同步（准生产-生产）
 */
@Service
@Transactional
public class SynScriptServiceImpl implements SynScriptService{

    @Autowired
    private MktScriptMapper mktScriptMapper;
    @Autowired
    private MktScriptPrdMapper mktScriptPrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;

    //同步表名
    private static final String tableName="mkt_script";


    /**
     * 同步单个接触脚本
     * @param scriptId
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeScript(Long scriptId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        Script script = mktScriptMapper.selectByPrimaryKey(scriptId);
        if(null==script){
            throw new SystemException("接触脚本信息不存在！");
        }
        Script script1 = mktScriptPrdMapper.selectByPrimaryKey(scriptId);
        if(null==script1){
            mktScriptPrdMapper.insert(script);
            synchronizeRecordService.addRecord(roleName,tableName,scriptId, SynchronizeType.add.getType());
        }else{
            mktScriptPrdMapper.updateByPrimaryKey(script);
            synchronizeRecordService.addRecord(roleName,tableName,scriptId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量接触脚本同步
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchScript(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<Script> prdList = mktScriptMapper.selectAll(null,null,null,null);
        List<Script> realList = mktScriptPrdMapper.selectAll(null,null,null,null);

        List<Script> addList=new ArrayList<Script>();
        List<Script> updateList=new ArrayList<Script>();
        List<Script> deleteList=new ArrayList<Script>();

        for(Script c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getScriptId()-realList.get(i).getScriptId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(Script c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getScriptId()-prdList.get(i).getScriptId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }

        //开始新增
        for(Script c:addList){
            mktScriptPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getScriptId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(Script c:updateList){
            mktScriptPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getScriptId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(Script c:deleteList){
            mktScriptPrdMapper.deleteByPrimaryKey(c.getScriptId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getScriptId(), SynchronizeType.delete.getType());
        }


        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }
}
