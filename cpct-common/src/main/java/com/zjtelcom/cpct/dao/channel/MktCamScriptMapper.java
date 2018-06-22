package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.CamScript;

import java.util.List;

public interface MktCamScriptMapper {
    int deleteByPrimaryKey(Long camScriptId);

    int insert(CamScript record);

    CamScript selectByPrimaryKey(Long camScriptId);

    List<CamScript> selectAll();

    int updateByPrimaryKey(CamScript record);
}