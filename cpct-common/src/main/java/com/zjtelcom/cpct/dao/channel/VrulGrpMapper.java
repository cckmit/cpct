package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.VrulGrp;

import java.util.List;

public interface VrulGrpMapper {
    int deleteByPrimaryKey(Long offerVrulGrpId);

    int insert(VrulGrp record);

    VrulGrp selectByPrimaryKey(Long offerVrulGrpId);

    List<VrulGrp> selectAll();

    int updateByPrimaryKey(VrulGrp record);
}