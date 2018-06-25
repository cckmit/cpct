package com.zjtelcom.cpct.service.impl.system;

import com.zjtelcom.cpct.dao.system.SysRoleMapper;
import com.zjtelcom.cpct.dao.system.SysRoleMenuMapper;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.domain.system.SysRoleMenu;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SysRoleServiceImpl extends BaseService implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;

    @Autowired
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Override
    public List<SysRole> listRole(Long roleId, String RoleName) {
        return sysRoleMapper.selectAll(roleId, RoleName);
    }

    @Override
    public int saveRole(SysRole sysRole) {
        //todo 判断字段是否为空

        //todo 判断角色名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysRole.setCreateStaff(loginId);
        sysRole.setCreateDate(new Date());

        return sysRoleMapper.insert(sysRole);
    }

    @Override
    public int updateRole(SysRole sysRole) {
        //todo 判断字段是否为空

        //todo 判断角色名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysRole.setUpdateStaff(loginId);
        sysRole.setUpdateDate(new Date());
        return sysRoleMapper.updateByPrimaryKey(sysRole);
    }

    @Override
    public SysRole getRole(Long id) {
        if (id == null) {
            //todo 为空异常
        }
        return sysRoleMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delRole(Long id) {
        return sysRoleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public void saveAuthority(Long roleId, List<Long> list) {
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

    }

}
