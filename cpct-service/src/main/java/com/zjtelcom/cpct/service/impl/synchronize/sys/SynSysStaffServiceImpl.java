package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysStaffMapper;
import com.zjtelcom.cpct.dao.system.SysStaffRoleMapper;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.domain.system.SysStaffRole;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysStaffService;
import com.zjtelcom.cpct_prd.dao.sys.SysStaffPrdMapper;
import com.zjtelcom.cpct_prd.dao.sys.SysStaffRolePrdMapper;
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
 * @Description:用户数据同步
 */
@Service
@Transactional
public class SynSysStaffServiceImpl implements SynSysStaffService{


    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private SysStaffPrdMapper sysStaffPrdMapper;
    @Autowired
    private SysStaffMapper sysStaffMapper;
    @Autowired
    private SysStaffRoleMapper sysStaffRoleMapper;
    @Autowired
    private SysStaffRolePrdMapper sysStaffRolePrdMapper;

    //同步表名
    private static final String tableName="sys_staff";


    /**
     * 同步单个用户
     * @param staffId    用户id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleStaff(Long staffId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        SysStaff sysStaff = sysStaffMapper.selectByPrimaryKey(staffId);
        if(sysStaff==null){
            throw new SystemException("对应用户信息不存在");
        }
        SysStaffRole sysStaffRole = sysStaffRoleMapper.selectByStaffId(sysStaff.getStaffId());

        //同步时查看是新增还是更新
        SysStaff sysStaff1 = sysStaffPrdMapper.selectByPrimaryKey(staffId);
        if(sysStaff1==null){
            sysStaffPrdMapper.insert(sysStaff);
            if(sysStaffRole!=null){
                sysStaffRolePrdMapper.insert(sysStaffRole);
            }
            synchronizeRecordService.addRecord(roleName,tableName,staffId, SynchronizeType.add.getType());
        }else{
            sysStaffPrdMapper.updateByPrimaryKey(sysStaff);
            if(sysStaffRole!=null){
                sysStaffRolePrdMapper.updateByPrimaryKey(sysStaffRole);
            }
            synchronizeRecordService.addRecord(roleName,tableName,staffId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 全表同步用户信息
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchStaff(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<SysStaff> prdList = sysStaffMapper.selectAll(new SysStaff());
        List<SysStaff> realList = sysStaffPrdMapper.selectAll(new SysStaff());
        //三个集合分别表示需要 新增的   修改的    删除的
        List<SysStaff> addList=new ArrayList<SysStaff>();
        List<SysStaff> updateList=new ArrayList<SysStaff>();
        List<SysStaff> deleteList=new ArrayList<SysStaff>();
        for(SysStaff c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getStaffId()-realList.get(i).getStaffId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(SysStaff c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getStaffId()-prdList.get(i).getStaffId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(SysStaff c:addList){
            sysStaffPrdMapper.insert(c);
            SysStaffRole sysStaffRole = sysStaffRoleMapper.selectByStaffId(c.getStaffId());
            if(sysStaffRole!=null){
                sysStaffRolePrdMapper.insert(sysStaffRole);
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getStaffId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(SysStaff c:updateList){
            sysStaffPrdMapper.updateByPrimaryKey(c);
            SysStaffRole sysStaffRole = sysStaffRoleMapper.selectByStaffId(c.getStaffId());
            if(sysStaffRole!=null){
                sysStaffRolePrdMapper.updateByPrimaryKey(sysStaffRole);
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getStaffId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(SysStaff c:deleteList){
            sysStaffPrdMapper.deleteByPrimaryKey(c.getStaffId());
            SysStaffRole sysStaffRole = sysStaffRoleMapper.selectByStaffId(c.getStaffId());
            if(sysStaffRole!=null){
                sysStaffRolePrdMapper.deleteByPrimaryKey(sysStaffRole.getStaffRoleId());
            }
            synchronizeRecordService.addRecord(roleName,tableName,c.getStaffId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    @Override
    public Map<String, Object> deleteSingleStaff(Long staffId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        sysStaffPrdMapper.deleteByPrimaryKey(staffId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        synchronizeRecordService.addRecord(roleName,tableName,staffId, SynchronizeType.delete.getType());
        return maps;
    }


}
