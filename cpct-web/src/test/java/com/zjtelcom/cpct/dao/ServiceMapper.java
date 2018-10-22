package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Service;
import java.util.List;

public interface ServiceMapper {
    int deleteByPrimaryKey(Long serviceId);

    int insert(Service record);

    Service selectByPrimaryKey(Long serviceId);

    List<Service> selectAll();

    int updateByPrimaryKey(Service record);
}