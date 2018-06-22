package com.zjtelcom.cpct.service.impl.system;

import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class SysParamsServiceImpl extends BaseService implements SysParamsService {

    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Override
    public List<SysParams> listParams(String paramName, Long configType) {
        return sysParamsMapper.selectAll(paramName, configType);
    }

    @Override
    public int saveParams(SysParams sysParams) {
        //todo 判断字段是否为空

        //todo 判断参数名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysParams.setCreateStaff(loginId);
        sysParams.setCreateDate(new Date());

        return sysParamsMapper.insert(sysParams);
    }

    @Override
    public int updateParams(SysParams sysParams) {
        //todo 判断字段是否为空

        //todo 判断参数名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysParams.setUpdateStaff(loginId);
        sysParams.setUpdateDate(new Date());
        return sysParamsMapper.updateByPrimaryKey(sysParams);
    }

    @Override
    public SysParams getParams(Long id) {
        if(id == null) {
            //todo 为空异常
        }
        return sysParamsMapper.selectByPrimaryKey(id);
    }

    @Override
    public int delParams(Long id) {
        return sysParamsMapper.deleteByPrimaryKey(id);
    }
}
