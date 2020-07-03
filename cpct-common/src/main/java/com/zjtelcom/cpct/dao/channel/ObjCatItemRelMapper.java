package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.ObjCatItemRel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ObjCatItemRelMapper {
    int deleteByPrimaryKey(Long relId);

    int insert(ObjCatItemRel record);

    ObjCatItemRel selectByPrimaryKey(Long relId);

    List<ObjCatItemRel> selectAll();

    int updateByPrimaryKey(ObjCatItemRel record);

    List<ObjCatItemRel> selectByObjId(Long objId);

    int deleteByCampaignId(@Param("campaignId") Long record);
}