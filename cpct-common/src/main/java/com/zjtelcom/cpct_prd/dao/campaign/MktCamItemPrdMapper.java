package com.zjtelcom.cpct_prd.dao.campaign;



import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamItemPrdMapper {
    int deleteByPrimaryKey(Long mktCamItemId);

    int insert(MktCamItem record);

    MktCamItem selectByPrimaryKey(Long mktCamItemId);

    List<MktCamItem> selectAll();

    int updateByPrimaryKey(MktCamItem record);

    int deleteByCampaignId(@Param("campaignId") Long campaignId);
}