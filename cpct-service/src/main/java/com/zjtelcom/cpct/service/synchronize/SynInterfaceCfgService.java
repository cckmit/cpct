package com.zjtelcom.cpct.service.synchronize;

import com.zjtelcom.cpct.domain.event.InterfaceCfg;

import java.util.Map;

public interface SynInterfaceCfgService {

    Map<String,Object> synchronizeSingleEventInterface(Long eventId,String roleName);

    Map<String,Object> synchronizeBatchEventInterface(String roleName);

}
