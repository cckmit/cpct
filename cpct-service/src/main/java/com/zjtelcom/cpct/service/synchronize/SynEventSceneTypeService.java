package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * @Description 同步事件场景目录
 * @Author pengy
 * @Date 2018/6/21 9:45
 */

public interface SynEventSceneTypeService {

    Map<String,Object> synchronizeSingleEventSceneType(Long eventSceneTypeId, String roleName);

    Map<String,Object> synchronizeBatchEventSceneType(String roleName);
}
