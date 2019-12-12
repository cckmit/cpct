package com.zjtelcom.cpct.service.cpct;

import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/12/09 15:15
 * @version: V1.0
 */
public interface ProjectManageService {

    Map<String, Object> updateProjectStateTime(Map<String, String> params);

    Map<String, Object> updateProjectPcState(Long mktCampaginId);

}