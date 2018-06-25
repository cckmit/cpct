package com.zjtelcom.cpct.service.impl.system;

import com.zjtelcom.cpct.dao.system.SysMenuMapper;
import com.zjtelcom.cpct.domain.system.SysMenu;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SysMenuServiceImpl extends BaseService implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> listMenu() {
        return sysMenuMapper.selectAll();
    }

    @Override
    public List<SysMenu> listMenuByRoleId(Long roleId) {
        return sysMenuMapper.selectByRoleId(roleId);
    }
}
