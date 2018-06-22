package com.zjtelcom.cpct.service.system;

import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.domain.system.SysRole;

import java.util.List;

public interface SysParamsService {

    List<SysParams> listParams(String paramName, Long configType);

    int saveParams(SysParams sysParams);

    int updateParams(SysParams sysParams);

    SysParams getParams(Long id);

    int delParams(Long id);

}
