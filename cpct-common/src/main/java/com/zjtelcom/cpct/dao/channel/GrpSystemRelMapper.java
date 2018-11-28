package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.GrpSystemRel;

import java.util.List;

public interface GrpSystemRelMapper {
    int deleteByPrimaryKey(Long grpSystemRelId);

    int insert(GrpSystemRel record);

    GrpSystemRel selectByPrimaryKey(Long grpSystemRelId);

    List<GrpSystemRel> selectAll();

    int updateByPrimaryKey(GrpSystemRel record);
}