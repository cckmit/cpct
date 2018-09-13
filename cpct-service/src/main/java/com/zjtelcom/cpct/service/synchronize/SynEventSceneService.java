package com.zjtelcom.cpct.service.synchronize;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/13
 * @Description:
 */
public interface SynEventSceneService {

    Map<String,Object> synchronizeSingleEventScene(Long eventSceneId, String roleName);

    Map<String,Object> synchronizeBatchEventScene(String roleName);
}
