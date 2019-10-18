package com.zjtelcom.cpct.dao.grouping;

import com.zjtelcom.cpct.dto.grouping.OrgGridRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OrgGridRelMapper {
    int deleteByPrimaryKey(Long orgGridRelId);

    int insert(OrgGridRel record);

    OrgGridRel selectByPrimaryKey(Long orgGridRelId);

    List<OrgGridRel> selectAll();

    int updateByPrimaryKey(OrgGridRel record);

    List<OrgGridRel> fuzzySelectByGridName(String gridName);
}