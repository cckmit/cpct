package com.zjtelcom.cpct.dao.channel;



import com.zjtelcom.cpct.domain.channel.ObjMktCampaignRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ObjMktCampaignRelMapper {
    int deleteByPrimaryKey(Long offerSceneRelId);

    int insert(ObjMktCampaignRel record);

    ObjMktCampaignRel selectByPrimaryKey(Long offerSceneRelId);

    List<ObjMktCampaignRel> selectAll();

    int updateByPrimaryKey(ObjMktCampaignRel record);

    List<ObjMktCampaignRel> selectByRequestIdAndType(@Param("requestId") Long requestId,@Param("type")String type);
}