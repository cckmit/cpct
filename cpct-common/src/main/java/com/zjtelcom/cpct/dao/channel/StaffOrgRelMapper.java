package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.StaffOrgRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface StaffOrgRelMapper {
    int deleteByPrimaryKey(Long staffOrgRelId);

    int insert(StaffOrgRel record);

    StaffOrgRel selectByPrimaryKey(Long staffOrgRelId);

    List<StaffOrgRel> selectAll();

    int updateByPrimaryKey(StaffOrgRel record);

    List<StaffOrgRel> selectByOrgId(Long aLong);

    List<Map<String,Object>> getStaffName(Long staffId);

    List<Long> getStaffByCode (@Param("staffCode") String staffCode);

}