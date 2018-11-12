package com.zjtelcom.cpct.service.impl.synchronize.sys;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.system.SysMenuMapper;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.sys.SynSysMenuService;
import com.zjtelcom.cpct_prd.dao.sys.SysMenuPrdMapper;
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
public class SynSysMenuServiceImpl implements SynSysMenuService {

    @Autowired
    private SynchronizeRecordService synchronizeRecordService;
    @Autowired
    private SysMenuPrdMapper sysMenuPrdMapper;
    @Autowired
    private SysMenuMapper sysMenuMapper;

    //同步表名
    private static final String tableName="sys_menu";

    /**
     * 同步单个菜单
     * @param menuId    菜单id
     * @param roleName   操作人身份
     * @return
     */
    @Override
    public Map<String, Object> synchronizeSingleMenu(Long menuId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        //查询源数据库
        SysMenu sysMenu = sysMenuMapper.selectByPrimaryKey(menuId);
        if(sysMenu==null){
            throw new SystemException("对应菜单信息不存在");
        }
        //同步时查看是新增还是更新
        SysMenu sysMenu1 = sysMenuPrdMapper.selectByPrimaryKey(menuId);
        if(sysMenu1==null){
            sysMenuPrdMapper.insert(sysMenu);
            synchronizeRecordService.addRecord(roleName,tableName,menuId, SynchronizeType.add.getType());
        }else{
            sysMenuPrdMapper.updateByPrimaryKey(sysMenu);
            synchronizeRecordService.addRecord(roleName,tableName,menuId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    /**
     * 批量同步菜单信息
     * @param roleName
     * @return
     */
    @Override
    public Map<String, Object> synchronizeBatchMenu(String roleName) {
        Map<String,Object> maps = new HashMap<>();
        List<SysMenu> prdList = sysMenuMapper.selectAll();
        List<SysMenu> realList = sysMenuPrdMapper.selectAll();
        //三个集合分别表示需要 新增的   修改的    删除的
        List<SysMenu> addList=new ArrayList<SysMenu>();
        List<SysMenu> updateList=new ArrayList<SysMenu>();
        List<SysMenu> deleteList=new ArrayList<SysMenu>();
        for(SysMenu c:prdList){
            for (int i = 0; i <realList.size() ; i++) {
                if(c.getMenuId()-realList.get(i).getMenuId()==0){
                    //需要修改的
                    updateList.add(c);
                    break;
                }else if(i==realList.size()-1){
                    //需要新增的  准生产存在，生产不存在
                    addList.add(c);
                }
            }
        }
        for(SysMenu c:realList){
            for (int i = 0; i <prdList.size() ; i++) {
                if(c.getMenuId()-prdList.get(i).getMenuId()==0){
                    break;
                }else if (i==prdList.size()-1){
                    //需要删除的   生产存在,准生产不存在
                    deleteList.add(c);
                }
            }
        }
        //开始新增
        for(SysMenu c:addList){
            sysMenuPrdMapper.insert(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getMenuId(), SynchronizeType.add.getType());
        }
        //开始修改
        for(SysMenu c:updateList){
            sysMenuPrdMapper.updateByPrimaryKey(c);
            synchronizeRecordService.addRecord(roleName,tableName,c.getMenuId(), SynchronizeType.update.getType());
        }
        //开始删除
        for(SysMenu c:deleteList){
            sysMenuPrdMapper.deleteByPrimaryKey(c.getMenuId());
            synchronizeRecordService.addRecord(roleName,tableName,c.getMenuId(), SynchronizeType.delete.getType());
        }

        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);

        return maps;
    }

    @Override
    public Map<String, Object> deleteSingleMenu(Long menuId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        sysMenuPrdMapper.deleteByPrimaryKey(menuId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        return maps;
    }


}
