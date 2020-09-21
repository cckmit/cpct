package com.zjtelcom.cpct.dao.grouping;

import com.zjtelcom.cpct.dto.grouping.OrgGridRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OrgGridRelMapper {
    int deleteByPrimaryKey(Long orgGridRelId);

    int insert(OrgGridRel record);

    OrgGridRel selectByPrimaryKey(Long orgGridRelId);

    List<OrgGridRel> selectAll();

    int updateByPrimaryKey(OrgGridRel record);

    List<OrgGridRel> fuzzySelectByGridName(String gridName);

    List<OrgGridRel> selectOrgGridByCode(@Param("list")List<String> codeList);

    Map<String, Object> getC3AndC4(@Param("attrib")String attrib);

    List<String>  getStaffByC3orC4(@Param("orgId")String orgId);

    List<String>  getStaffByOrgPath(@Param("orgPath")String orgPath);

    String  getOrgPathByOrgId(@Param("orgId")String orgId);


}