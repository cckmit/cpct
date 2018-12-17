package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.CamScript;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamScriptMapper {
    int deleteByPrimaryKey(Long camScriptId);

    int insert(CamScript record);

    CamScript selectByPrimaryKey(Long camScriptId);

    CamScript selectByConfId(@Param("evtContactConfId")Long evtContactConfId);

    List<CamScript> selectAll();

    int updateByPrimaryKey(CamScript record);

    List<CamScript> selectByCampaignId(Long mktCampaignId);
}