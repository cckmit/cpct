package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.GrpSystemRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface GrpSystemRelMapper {
    int deleteByPrimaryKey(Long grpSystemRelId);

    int insert(GrpSystemRel record);

    GrpSystemRel selectByPrimaryKey(Long grpSystemRelId);

    List<GrpSystemRel> selectAll();

    int updateByPrimaryKey(GrpSystemRel record);

    GrpSystemRel selectByOfferId(@Param("offerId") Long offerId);
}