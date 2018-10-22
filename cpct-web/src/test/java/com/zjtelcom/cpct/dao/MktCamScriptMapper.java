package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCamScript;
import java.util.List;

public interface MktCamScriptMapper {
    int deleteByPrimaryKey(Long mktCampaignScptId);

    int insert(MktCamScript record);

    MktCamScript selectByPrimaryKey(Long mktCampaignScptId);

    List<MktCamScript> selectAll();

    int updateByPrimaryKey(MktCamScript record);
}