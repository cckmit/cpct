package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.MktCamChlConf;
import java.util.List;

public interface MktCamChlConfMapper {
    int deleteByPrimaryKey(Long evtContactConfId);

    int insert(MktCamChlConf record);

    MktCamChlConf selectByPrimaryKey(Long evtContactConfId);

    List<MktCamChlConf> selectAll();

    int updateByPrimaryKey(MktCamChlConf record);
}