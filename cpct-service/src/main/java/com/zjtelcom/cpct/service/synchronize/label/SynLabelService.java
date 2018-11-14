package com.zjtelcom.cpct.service.synchronize.label;

import java.util.Map;
/**
 * @Auther: anson
 * @Date: 2018/9/14
 * @Description:标签同步
 */
public interface SynLabelService {

    Map<String,Object> synchronizeSingleLabel(Long labelId,String roleName);

    Map<String,Object> synchronizeBatchLabel(String roleName);

    Map<String,Object> deleteSingleLabel(Long labelId,String roleName);
}
