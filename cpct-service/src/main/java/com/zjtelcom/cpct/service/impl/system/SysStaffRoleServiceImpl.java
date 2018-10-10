package com.zjtelcom.cpct.service.impl.system;

import com.zjtelcom.cpct.dao.system.SysStaffRoleMapper;
import com.zjtelcom.cpct.domain.system.SysStaffRole;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysStaffRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SysStaffRoleServiceImpl extends BaseService implements SysStaffRoleService {

    @Autowired
    private SysStaffRoleMapper sysStaffRoleMapper;

    @Override
    public int saveStaffRole(Long staffId, Long roleId) {
        //todo 判空

        SysStaffRole sysStaffRole = new SysStaffRole();
        sysStaffRole.setStaffId(staffId);
        sysStaffRole.setRoleId(roleId);

        return sysStaffRoleMapper.insert(sysStaffRole);
    }

    @Override
    public int delStaffRoleByStaffId(Long staffId) {
        //todo 判空

        return sysStaffRoleMapper.deleteByPrimaryKey(staffId);
    }
}
