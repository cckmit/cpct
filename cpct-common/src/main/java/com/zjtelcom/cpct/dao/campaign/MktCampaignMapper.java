package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCampaignCountDO;
import com.zjtelcom.cpct.domain.campaign.MktCampaignDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
public interface MktCampaignMapper {
    int deleteByPrimaryKey(Long mktCampaignId);

    int insert(MktCampaignDO mktCampaignDO);

    MktCampaignDO selectByPrimaryKey(Long mktCampaignId);

    List<MktCampaignDO> selectAll();

    int updateByPrimaryKey(MktCampaignDO mktCampaignDO);

    List<MktCampaignCountDO> qryMktCampaignListPage(MktCampaignDO mktCampaignDO);

    List<MktCampaignCountDO> qryMktCampaignListPage4Sync(@Param("map") Map<String,Object> map);

    void changeMktCampaignStatus(@Param("mktCampaignId")Long mktCampaignId, @Param("statusCd")String statusCd, @Param("updateDate")Date updateDate, @Param("updateStaff")Long updateStaff);

    List<MktCampaignDO> qryMktCampaignListByCondition(MktCampaignDO mktCampaignDO);

}