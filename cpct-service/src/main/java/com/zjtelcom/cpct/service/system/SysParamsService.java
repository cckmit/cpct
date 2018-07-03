package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysRole;

import java.util.List;
import java.util.Map;

public interface SysParamsService {

    Map<String,Object> listParams(String paramName, Long configType,Integer page,Integer pageSize);

    Map<String,Object> saveParams(SysParams sysParams);

    Map<String,Object> updateParams(SysParams sysParams);

    Map<String,Object> getParams(Long id);

    Map<String,Object> delParams(Long id);

    Map<String,Object> listParamsByKey(String key);



}
