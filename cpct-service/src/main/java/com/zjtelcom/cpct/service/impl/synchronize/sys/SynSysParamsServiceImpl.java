package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysParamsService;
import com.zjtelcom.cpct_prd.dao.sys.SysParamsPrdMapper;
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
 * @Date: 2018/9/17
 * @Description:
 */
@Service
@Transactional
public class SynSysParamsServiceImpl implements SynSysParamsService {

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private SysParamsPrdMapper sysParamsPrdMapper;
    @Autowired
    private SysParamsMapper sysParamsMapper;

    //同步表名
    private static final String tableName="sys_params";

    /**
     * 同步单个静态参数
     * @param paramId    静态参数id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleParam(Long paramId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        List<SysParams> sysParams = sysParamsMapper.selectByPrimaryKey(paramId);
        if(sysParams.isEmpty()){
            throw new SystemException("对应静态参数不存在");
        }
        //同步时查看是新增还是更新
        List<SysParams> sysParams1 = sysParamsPrdMapper.selectByPrimaryKey(paramId);
        if(sysParams1.isEmpty()){
            sysParamsPrdMapper.insert(sysParams.get(0));
            synchronizeRecordService.addRecord(roleName,tableName,paramId, SynchronizeType.add.getType());
        }else{
            sysParamsPrdMapper.updateByPrimaryKey(sysParams.get(0));
            synchronizeRecordService.addRecord(roleName,tableName,paramId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量同步静态参数
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchParam(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<SysParams> prdList = sysParamsMapper.selectAll(null,null);
        List<SysParams> realList = sysParamsPrdMapper.selectAll(null,null);
        //三个集合分别表示需要 新增的   修改的    删除的
        List<SysParams> addList=new ArrayList<SysParams>();
        List<SysParams> updateList=new ArrayList<SysParams>();
        List<SysParams> deleteList=new ArrayList<SysParams>();
        for(SysParams c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getParamId()-realList.get(i).getParamId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(SysParams c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getParamId()-prdList.get(i).getParamId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(SysParams c:addList){
            sysParamsPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getParamId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(SysParams c:updateList){
            sysParamsPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getParamId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(SysParams c:deleteList){
            sysParamsPrdMapper.deleteByPrimaryKey(c.getParamId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getParamId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }


}
