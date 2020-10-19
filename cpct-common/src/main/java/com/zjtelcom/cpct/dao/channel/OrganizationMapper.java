package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.Organization;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

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

    List<Map<String,String>> fuzzySelectByName(@Param("parentIds")List<String> areaIds,@Param("orgName")String fuzzyField);

    List<Map<String,Object>> getStaffOrgId(@Param("staffId") Long staffId);

    List<Organization> selectByParentIdForLevelFive(Long orgId);

    List<Organization> selectMenuForLevelFive();

    List<Organization> getByOrgNameC4(@Param("orgNameC4")String orgNameC4);

    List<Organization> getByOrgNameC5(@Param("orgNameC5")String orgNameC5);

    List<Organization> selectMenuByEleven();

    Long getByOrgid4a(@Param("orgId4a") Long orgId4a);

    List<Map<String, Object>> getStaffIdAndType(Long orgId);

    int getCount(@Param("staffId") Long staffId);
}