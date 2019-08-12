package com.zjtelcom.cpct.dao.grouping;

import com.zjtelcom.cpct.domain.grouping.ServicePackage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/08/12 10:33
 * @version: V1.0
 */
@Mapper
@Repository
public interface ServicePackageMapper {
    int deleteByPrimaryKey(Long servicePackageId);

    int insert(ServicePackage servicePackage);

    ServicePackage selectByPrimaryKey(Long servicePackageId);

    List<ServicePackage> selectAll();

    List<ServicePackage> selectByName(@Param("servicePackageName") String servicePackageName);

    int updateByPrimaryKey(ServicePackage servicePackage);
}