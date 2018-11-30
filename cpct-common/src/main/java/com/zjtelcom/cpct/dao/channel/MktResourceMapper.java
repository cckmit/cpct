package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktResource;

import java.util.List;

public interface MktResourceMapper {
    int deleteByPrimaryKey(Long mktResId);

    int insert(MktResource record);

    MktResource selectByPrimaryKey(Long mktResId);

    List<MktResource> selectAll();

    int updateByPrimaryKey(MktResource record);
}