package com.zjtelcom.cpct_prd.dao.channel;

import com.zjtelcom.cpct.domain.channel.ServiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ServicePrdMapper {
    int deleteByPrimaryKey(Long serviceId);

    int insert(ServiceEntity record);

    ServiceEntity selectByPrimaryKey(Long serviceId);

    List<ServiceEntity> selectAll();

    int updateByPrimaryKey(ServiceEntity record);

    List<ServiceEntity> selectByServiceName(@Param("serviceName") String serviceName);

    List<ServiceEntity> selectDetailByServiceEntity(ServiceEntity record);
}
