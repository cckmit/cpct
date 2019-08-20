package com.zjtelcom.cpct.dao.campaign;


import com.zjtelcom.cpct.domain.campaign.MktCamDisplayColumnRel;
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

    List<MktCampaignCountDO> qryMktCampaignListPageForNoPublish(MktCampaignDO mktCampaignDO);

    List<MktCampaignCountDO> qryMktCampaignListPageForPublish(MktCampaignDO mktCampaignDO);

    List<MktCampaignCountDO> qryMktCampaignListPage4Sync(@Param("map") Map<String,Object> map);

    void changeMktCampaignStatus(@Param("mktCampaignId")Long mktCampaignId, @Param("statusCd")String statusCd, @Param("updateDate")Date updateDate, @Param("updateStaff")Long updateStaff);

    List<MktCampaignDO> qryMktCampaignListByCondition(MktCampaignDO mktCampaignDO);

    List<MktCampaignDO> qryMktCampaignListByTypeAndStatus(@Param("execType") String execType, @Param("statusCd") String statusCd);

    MktCampaignDO selectByRuleId(@Param("ruleId") Long ruleId);

    List<MktCamDisplayColumnRel> selectAllGroupByCamId();

    MktCampaignDO selectByInitId(@Param("initId")Long initId);

    MktCampaignDO selectPrimaryKeyByInitId(@Param("initId")Long initId, @Param("statusCd")String statusCd);

    MktCampaignDO selectByInitForRollBack(@Param("initId")Long initId);

    int countByStatus(@Param("map")Map<String, Object> map);

    int countByTrial(@Param("map")Map<String, Object> map);

}