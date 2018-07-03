package com.zjtelcom.cpct.service.impl.system;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysRole;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SysParamsServiceImpl extends BaseService implements SysParamsService {

    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Override
    public Map<String,Object> listParams(String paramName, Long configType,Integer page,Integer pageSize) {
        Map<String,Object> result = new HashMap<>();
        PageHelper.startPage(page,pageSize);
        List<SysParams> list = sysParamsMapper.selectAll(paramName, configType);
        Page pageInfo = new Page(new PageInfo(list));

        result.put("resultCode","0");
        result.put("resultMsg","");
        result.put("data",list);
        result.put("pageInfo",pageInfo);

        return result;
    }

    @Override
    public Map<String,Object> saveParams(SysParams sysParams) {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断参数名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysParams.setCreateStaff(loginId);
        sysParams.setCreateDate(new Date());

        int flag = sysParamsMapper.insert(sysParams);
        result.put("resultCode","0");

        return result;
    }

    @Override
    public Map<String,Object> updateParams(SysParams sysParams) {
        Map<String,Object> result = new HashMap<>();
        //todo 判断字段是否为空

        //todo 判断参数名是否重复

        //todo 获取当前登录用户id
        Long loginId = 1L;
        sysParams.setUpdateStaff(loginId);
        sysParams.setUpdateDate(new Date());
        int flag = sysParamsMapper.updateByPrimaryKey(sysParams);
        result.put("resultCode","0");

        return result;
    }

    @Override
    public Map<String,Object> getParams(Long id) {
        Map<String,Object> result = new HashMap<>();
        if(id == null) {
            //todo 为空异常
        }
        SysParams sysParams = sysParamsMapper.selectByPrimaryKey(id);
        result.put("resultCode","0");
        result.put("data",sysParams);

        return result;
    }

    @Override
    public Map<String,Object> delParams(Long id) {
        Map<String,Object> result = new HashMap<>();

        //todo 验证是否可以删除

        sysParamsMapper.deleteByPrimaryKey(id);
        result.put("resultCode","0");

        return result;
    }

    @Override
    public Map<String, Object> listParamsByKey(String key) {
        Map<String,Object> result = new HashMap<>();

        List<Map<String,String>> list = sysParamsMapper.listParamsByKey(key);

        result.put("resultCode","0");
        result.put("data",list);

        return result;
    }
}
