package com.zjtelcom.cpct_offer.dao.inst;


import com.zjtelcom.cpct.domain.channel.RequestInstRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface RequestInstRelMapper {
    int deleteByPrimaryKey(Long requestInstRelId);

    int insert(RequestInstRel record);

    RequestInstRel selectByPrimaryKey(Long requestInstRelId);

    List<RequestInstRel> selectAll();

    int updateByPrimaryKey(RequestInstRel record);

    List<RequestInstRel> selectByRequestId(@Param("requestId") Long requestId,@Param("type")String type);

    int deleteByCampaignId(@Param("requestInfoId") Long requestId,@Param("requestObjId") Long campaignId);

    List<RequestInstRel> selectByCampaignId(@Param("requestObjId") Long requestObjId,@Param("type")String type);

    int insertInfo(RequestInstRel record);


}