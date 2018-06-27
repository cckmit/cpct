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

    List<CamScript> selectAll(@Param("campaignId")Long campaignId,@Param("evtContactConfId")Long evtContactConfId);

    int updateByPrimaryKey(CamScript record);
}