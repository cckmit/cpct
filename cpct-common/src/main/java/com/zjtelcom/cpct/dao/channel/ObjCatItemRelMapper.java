package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.ObjCatItemRel;

import java.util.List;

public interface ObjCatItemRelMapper {
    int deleteByPrimaryKey(Long relId);

    int insert(ObjCatItemRel record);

    ObjCatItemRel selectByPrimaryKey(Long relId);

    List<ObjCatItemRel> selectAll();

    int updateByPrimaryKey(ObjCatItemRel record);
}