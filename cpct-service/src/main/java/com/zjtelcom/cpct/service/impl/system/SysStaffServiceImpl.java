package com.zjtelcom.cpct.service.impl.system;

import com.zjtelcom.cpct.dao.system.SysStaffMapper;
import com.zjtelcom.cpct.domain.system.SysStaff;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SysStaffServiceImpl extends BaseService implements SysStaffService {

    @Autowired
    private SysStaffMapper sysStaffMapper;

    @Override
    public List<SysStaff> listStaff(String staffCode,String staffName,Long status) {
        return sysStaffMapper.selectAll(staffCode,staffName,status);
    }

    @Override
    public int saveStaff(SysStaff sysStaff) {
        //todo 判断字段是否为空

        //todo 判断账号是否重复

        //todo 密码加密

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysStaff.setCreateStaff(loginId);
        sysStaff.setCreateDate(new Date());

        return sysStaffMapper.insert(sysStaff);
    }

    @Override
    public int updateStaff(SysStaff sysStaff) {
        //todo 判断字段是否为空

        //todo 判断账号是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysStaff.setUpdateStaff(loginId);
        sysStaff.setUpdateDate(new Date());
        return sysStaffMapper.updateByPrimaryKey(sysStaff);
    }

    @Override
    public int changeStatus(Long id, Long status) {
        if(id == null || status == null) {
            // todo 异常
        }
        SysStaff params = new SysStaff();
        params.setStaffId(id);
        params.setStatus(status);

        //todo 获取当前登录用户id
        Long loginId = 1L;
        params.setUpdateStaff(loginId);
        params.setUpdateDate(new Date());
        return sysStaffMapper.changeStatus(params);
    }

    @Override
    public SysStaff getStaff(Long id) {

        if(id == null) {
            //todo 为空异常
        }
        return sysStaffMapper.selectByPrimaryKey(id);
    }
}
