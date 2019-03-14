package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.ServiceType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ServiceTypeMapper {
    int deleteByPrimaryKey(Long serviceTypeId);

    int insert(ServiceType record);

    ServiceType selectByPrimaryKey(Long serviceTypeId);

    List<ServiceType> selectAll();

    int updateByPrimaryKey(ServiceType record);

    List<ServiceType> findServiceTypeListByPar(@Param("parServiceTypeId") Long parServiceTypeId);

    List<ServiceType> getServiceTypeByConditon(ServiceType serviceType);
}