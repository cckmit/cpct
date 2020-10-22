package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.MktCamResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamResourceMapper {
    int deleteByPrimaryKey(Long mktCamResourceId);

    int deleteByCampaignId(Long mktCampaignId);

    int insert(MktCamResource record);

    MktCamResource selectByPrimaryKey(Long mktCamResourceId);

    List<MktCamResource> selectByCampaignId(@Param("mktCampaignId") Long mktCampaignId,@Param("frameFlg") String frameFlg,@Param("ruleId")Long ruleId);

    MktCamResource selectByRuleId(@Param("ruleId") Long rule,@Param("frameFlg")String frameFlg);

    List<MktCamResource> selectAll();

    int updateByPrimaryKey(MktCamResource record);

    int updateResourceId(Long mktCamResourceId, Long resourceId);

    List<MktCamResource> listPage(MktCamResource record);
}