package com.zjtelcom.cpct.service.synchronize.label;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/9/17
 * @Description:
 */
public interface SynMessageLabelService {

    Map<String,Object> synchronizeSingleMessageLabel(Long messageLabelId, String roleName);

    Map<String,Object> synchronizeBatchMessageLabel(String roleName);

    Map<String,Object> deleteSingleMessageLabel(Long messageLabelId, String roleName);
}
