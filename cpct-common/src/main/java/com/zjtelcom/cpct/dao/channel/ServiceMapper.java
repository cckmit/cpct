package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.ServiceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface ServiceMapper {
    int deleteByPrimaryKey(Long serviceId);

    int insert(ServiceEntity record);

    ServiceEntity selectByPrimaryKey(Long mktResId);

    List<ServiceEntity> selectAll();

    int updateByPrimaryKey(ServiceEntity record);

    List<ServiceEntity> selectByServiceName(@Param("serviceName") String serviceName);

    List<ServiceEntity> selectDetailByServiceEntity(ServiceEntity record);
}
