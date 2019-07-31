package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.StaffOrgRel;

import java.util.List;

public interface StaffOrgRelMapper {
    int deleteByPrimaryKey(Long staffOrgRelId);

    int insert(StaffOrgRel record);

    StaffOrgRel selectByPrimaryKey(Long staffOrgRelId);

    List<StaffOrgRel> selectAll();

    int updateByPrimaryKey(StaffOrgRel record);
}