package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCamEvtRel;
import java.util.List;

public interface MktCamEvtRelMapper {
    int deleteByPrimaryKey(Long mktCampEvtRelId);

    int insert(MktCamEvtRel record);

    MktCamEvtRel selectByPrimaryKey(Long mktCampEvtRelId);

    List<MktCamEvtRel> selectAll();

    int updateByPrimaryKey(MktCamEvtRel record);
}