package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.ObjMktCampaignRel;

import java.util.List;

public interface ObjMktCampaignRelMapper {
    int deleteByPrimaryKey(Long offerSceneRelId);

    int insert(ObjMktCampaignRel record);

    ObjMktCampaignRel selectByPrimaryKey(Long offerSceneRelId);

    List<ObjMktCampaignRel> selectAll();

    int updateByPrimaryKey(ObjMktCampaignRel record);
}