package com.zjtelcom.cpct.service.synchronize.label;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/28
 * @Description:标签组同步
 */
public interface SynLabelGrpService {

    Map<String,Object> synchronizeSingleLabel(Long labelGrpId, String roleName);

    Map<String,Object> synchronizeBatchLabel(String roleName);

    Map<String,Object> deleteSingleLabel(Long labelGrpId, String roleName);
}
