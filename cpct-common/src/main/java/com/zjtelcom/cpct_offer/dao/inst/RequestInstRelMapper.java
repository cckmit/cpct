package com.zjtelcom.cpct_offer.dao.inst;


import com.zjtelcom.cpct.domain.channel.RequestInstRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RequestInstRelMapper {
    int deleteByPrimaryKey(Long requestInstRelId);

    int insert(RequestInstRel record);

    RequestInstRel selectByPrimaryKey(Long requestInstRelId);

    List<RequestInstRel> selectAll();

    int updateByPrimaryKey(RequestInstRel record);

    List<RequestInstRel> selectByRequestId(@Param("requestId") Long requestId,@Param("type")String type);
}