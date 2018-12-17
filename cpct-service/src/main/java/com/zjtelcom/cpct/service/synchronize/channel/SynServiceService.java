package com.zjtelcom.cpct.service.synchronize.channel;

import java.util.Map;

public interface SynServiceService {

    Map<String,Object> synchronizeSingleService(Long serviceId, String roleName);

    Map<String,Object> deleteSingleService(Long serviceId, String roleName);

}
