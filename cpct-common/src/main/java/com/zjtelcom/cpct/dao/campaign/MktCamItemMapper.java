package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCamItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamItemMapper {
    int deleteByPrimaryKey(Long mktCamItemId);

    int insert(MktCamItem record);

    MktCamItem selectByPrimaryKey(Long mktCamItemId);

    List<MktCamItem> selectAll();

    int updateByPrimaryKey(MktCamItem record);

    int insertByBatch(List<MktCamItem> record);

    List<MktCamItem> selectByBatch(List<Long> mktCamItemIdList);

    List<MktCamItem> selectByCampaignAndType(@Param("campaignId")Long campaignId,@Param("type")String itemType);

    List<MktCamItem> selectByCampaignId(@Param("campaignId")Long campaignId);

    MktCamItem selectByItemIdAndCampaignId(@Param("itemId")Long itemId,@Param("campaignId")Long campaignId);
}