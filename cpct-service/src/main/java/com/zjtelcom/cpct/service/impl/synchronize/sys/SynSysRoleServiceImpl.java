package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysRoleMapper;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysRoleService;
import com.zjtelcom.cpct_prd.dao.sys.SysRolePrdMapper;
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
public class SynSysRoleServiceImpl implements SynSysRoleService {

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private SysRolePrdMapper sysRolePrdMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;

    //同步表名
    private static final String tableName="sys_role";


    /**
     * 同步单个角色
     * @param roleId    角色id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleRole(Long roleId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(roleId);
        if(sysRole==null){
            throw new SystemException("对应角色信息不存在");
        }
        //同步时查看是新增还是更新
        SysRole sysRole1 = sysRolePrdMapper.selectByPrimaryKey(roleId);
        if(sysRole1==null){
            sysRolePrdMapper.insert(sysRole);
            synchronizeRecordService.addRecord(roleName,tableName,roleId, SynchronizeType.add.getType());
        }else{
            sysRolePrdMapper.updateByPrimaryKey(sysRole);
            synchronizeRecordService.addRecord(roleName,tableName,roleId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     *  批量同步角色信息
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchRole(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //先查出准生产的所有事件
        List<SysRole> prdList = sysRoleMapper.selectByParams(null,null);
        //查出生产的所有事件
        List<SysRole> realList = sysRolePrdMapper.selectByParams(null,null);
        //三个集合分别表示需要 新增的   修改的    删除的
        List<SysRole> addList=new ArrayList<SysRole>();
        List<SysRole> updateList=new ArrayList<SysRole>();
        List<SysRole> deleteList=new ArrayList<SysRole>();
        for(SysRole c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getRoleId()-realList.get(i).getRoleId()==0){
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
        for(SysRole c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getRoleId()-prdList.get(i).getRoleId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(SysRole c:addList){
            sysRolePrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getRoleId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(SysRole c:updateList){
            sysRolePrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getRoleId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(SysRole c:deleteList){
            sysRolePrdMapper.deleteByPrimaryKey(c.getRoleId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getRoleId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

}
