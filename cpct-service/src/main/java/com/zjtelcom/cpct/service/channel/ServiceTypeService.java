package com.zjtelcom.cpct.service.channel;

import com.zjtelcom.cpct.domain.channel.ServiceType;

import java.util.Map;

public interface ServiceTypeService {

    Map<String, Object> getServiceTypeList();

    Map<String, Object> getServiceTypeByCondition(Long userId, Map<String,Object> params);

    Map<String, Object> createServiceType(Long userId, ServiceType addVO);

    Map<String, Object> modServiceType(Long userId, ServiceType editVO);

    Map<String, Object> delServiceType(Long userId, ServiceType serviceType);

    Map<String, Object> getServiceTypeDetail(Long userId, Long serviceTypeId);

}