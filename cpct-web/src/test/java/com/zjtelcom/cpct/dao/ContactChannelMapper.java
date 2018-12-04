package com.zjtelcom.cpct.dao;

import com.zjtelcom.cpct.domain.ContactChannel;

import java.util.List;

public interface ContactChannelMapper {
    int deleteByPrimaryKey(Long contactChlId);

    int insert(ContactChannel record);

    ContactChannel selectByPrimaryKey(Long contactChlId);

    List<ContactChannel> selectAll();

    int updateByPrimaryKey(ContactChannel record);
}