package com.zjtelcom.cpct.dao.system;


import com.zjtelcom.cpct.domain.system.SysStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysStaffMapper {

    int deleteByPrimaryKey(Long staffId);

    int insert(SysStaff record);

    SysStaff selectByPrimaryKey(Long staffId);

    List<SysStaff> selectAll(@Param("staffCode") String staffCode,
                             @Param("staffName") String staffName,
                             @Param("status") Long status);

    int updateByPrimaryKey(SysStaff record);

    int changeStatus(SysStaff record);

    int updatePassword(@Param("staffId") Long staffId,
                       @Param("password") String password);

}