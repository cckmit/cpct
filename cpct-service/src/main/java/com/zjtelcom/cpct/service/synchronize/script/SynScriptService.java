package com.zjtelcom.cpct.service.synchronize.script;

import java.util.Map;
/**
 * @Auther: anson
 * @Date: 2018/9/14
 * @Description:接触脚本同步
 */
public interface SynScriptService {

    Map<String,Object> synchronizeScript(Long scriptId,String roleName);

    Map<String,Object> synchronizeBatchScript(String roleName);






}
