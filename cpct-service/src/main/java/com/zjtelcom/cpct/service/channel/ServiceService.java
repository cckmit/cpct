package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.ServiceEntity;

import java.util.Map;

public interface ServiceService {

    Map<String, Object> getServiceList(Long userId, Map<String,Object> params);

    Map<String, Object> getServiceListByName(Long userId, Map<String,Object> params);

    Map<String, Object> createService(Long userId, ServiceEntity addVO);

    Map<String, Object> modService(Long userId, ServiceEntity editVO);

    Map<String, Object> delService(Long userId, ServiceEntity serviceEntity);

    Map<String, Object> getServiceDetail(Long userId, Long serviceId);
}
