package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.Channel;

import java.util.List;

public interface ContactChannelMapper {
    int deleteByPrimaryKey(Long contactChlId);

    int insert(Channel record);

    Channel selectByPrimaryKey(Long contactChlId);

    List<Channel> selectAll();

    int updateByPrimaryKey(Channel record);
}