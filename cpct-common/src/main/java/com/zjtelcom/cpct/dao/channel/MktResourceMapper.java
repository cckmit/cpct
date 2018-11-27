package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktResource;

import java.util.List;

public interface MktResourceMapper {
    int deleteByPrimaryKey(Integer mktResId);

    int insert(MktResource record);

    MktResource selectByPrimaryKey(Integer mktResId);

    List<MktResource> selectAll();

    int updateByPrimaryKey(MktResource record);
}