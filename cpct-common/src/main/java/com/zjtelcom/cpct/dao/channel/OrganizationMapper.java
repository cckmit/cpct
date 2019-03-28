package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrganizationMapper {
    int deleteByPrimaryKey(Long orgId);

    int insert(Organization record);

    Organization selectByPrimaryKey(Long orgId);

    List<Organization> selectAll();

    int updateByPrimaryKey(Organization record);

    List<Organization> selectByParentId(@Param("parentId")Long parentId);

    List<Organization> selectMenu();

    Organization selectBy4aId(@Param("areaId") Long areaId);

    List<Long> selectByIdList(@Param("list")List<Long> idList);
}