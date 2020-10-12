package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktRequestDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MktRequestMapper {
    MktRequestDO getRequestInfoByMktId(@Param("requestType") String requestType, @Param("nodeId") String nodeId, @Param("mktCamId") Long mktCamId);
}
