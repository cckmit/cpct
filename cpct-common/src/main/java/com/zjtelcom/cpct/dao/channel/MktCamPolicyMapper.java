package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.MktCamPolicy;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MktCamPolicyMapper {
    int deleteByPrimaryKey(Long mktCamPolicyId);

    int insert(MktCamPolicy record);

    MktCamPolicy selectByPrimaryKey(Long mktCamPolicyId);

    List<MktCamPolicy> selectAll();

    int updateByPrimaryKey(MktCamPolicy record);

    int deleteByCampaignId(@Param("campaignId") Long campaignId);

    List<MktCamPolicy> selectByCampaignId(@Param("campaignId") Long requestId);


}