package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: linchao
 * @date: 2018/09/06 16:14
 * @version: V1.0
 */
@Mapper
@Repository
public interface MktCampaignReportMapper {

    int countByStatus(@Param("map") Map<String, Object> map);

    int countByTime(@Param("map") Map<String, Object> map);

    List<MktCampaignDO> selectByStatus(@Param("map") Map<String, Object> map);

    List<MktCampaignDO> selectByTrial(@Param("map")Map<String, Object> map);

    List<Map<String, Object>> selectCamSumByArea(@Param("map") Map<String, Object> map);

    Map<String, Object> selectCamSumByArea1(@Param("map") Map<String, Object> map);

}