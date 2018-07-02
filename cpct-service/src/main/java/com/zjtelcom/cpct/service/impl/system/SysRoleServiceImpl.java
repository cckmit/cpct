package com.zjtelcom.cpct.service.impl.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.system.SysRoleMapper;
import com.zjtelcom.cpct.dao.system.SysRoleMenuMapper;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.domain.system.SysRoleMenu;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class SysRoleServiceImpl extends BaseService implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public Map<String, Object> listRole(Long roleId, String RoleName,int page,int pageSize) {
        Map<String,Object> result = new HashMap<>();
        PageHelper.startPage(page,pageSize);
        List<SysRole> list = sysRoleMapper.selectAll(roleId, RoleName);
        Page pageInfo = new Page(new PageInfo(list));

        result.put("resultCode","0");
        result.put("list",list);
        result.put("pageInfo",pageInfo);

        return result;
    }

    @Override
    public Map<String, Object> saveRole(SysRole sysRole) {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断角色名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysRole.setCreateStaff(loginId);
        sysRole.setCreateDate(new Date());

        sysRoleMapper.insert(sysRole);
        result.put("resultCode","0");
        return result;
    }

    @Override
    public Map<String, Object> updateRole(SysRole sysRole) {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断角色名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysRole.setUpdateStaff(loginId);
        sysRole.setUpdateDate(new Date());
        sysRoleMapper.updateByPrimaryKey(sysRole);
        result.put("resultCode","0");
        return result;
    }

    @Override
    public Map<String, Object> getRole(Long id) {
        Map<String,Object> result = new HashMap<>();
        if (id == null) {
            //todo 为空异常
        }
        SysRole sysRole = sysRoleMapper.selectByPrimaryKey(id);
        result.put("resultCode","0");
        result.put("data",sysRole);
        return result;
    }

    @Override
    public Map<String, Object> delRole(Long id) {
        Map<String,Object> result = new HashMap<>();
        sysRoleMapper.deleteByPrimaryKey(id);
        result.put("resultCode","0");
        return result;
    }

    @Override
    public Map<String, Object> saveAuthority(Long roleId, List<Long> list) {
        Map<String,Object> result = new HashMap<>();
        //todo 验证参数null
//        List<SysRoleMenu> paramsList = new ArrayList<>();
        SysRoleMenu sysRoleMenu = new SysRoleMenu();
        sysRoleMenu.setRoleId(roleId);
        for (Long menuId : list) {
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenuMapper.insert(sysRoleMenu);
        }

//        SysRoleMenu sysRoleMenu;
//        for (Long menuId : list) {
//            sysRoleMenu = new SysRoleMenu();
//            sysRoleMenu.setRoleId(roleId);
//            sysRoleMenu.setMenuId(menuId);
//            paramsList.add(sysRoleMenu);
//        }
        result.put("resultCode","0");
        return result;
    }

}
