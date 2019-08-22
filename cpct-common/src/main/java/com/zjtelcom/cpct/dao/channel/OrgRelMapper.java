package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.OrgRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrgRelMapper {
    int deleteByPrimaryKey(Long orgRelId);

    int insert(OrgRel record);

    OrgRel selectByPrimaryKey(Long orgRelId);

    List<OrgRel> selectAll();

    int updateByPrimaryKey(OrgRel record);

    List<OrgRel> selectByAOrgId(@Param("AorgId")String orgNameC4);
}