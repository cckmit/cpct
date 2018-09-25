package com.zjtelcom.cpct_prd.dao.sys;


import com.zjtelcom.cpct.domain.system.SysStaff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface SysStaffPrdMapper {


    int deleteByPrimaryKey(Long staffId);

    int insert(SysStaff record);

    SysStaff selectByPrimaryKey(Long staffId);

    List<SysStaff> selectAll(SysStaff record);

    int updateByPrimaryKey(SysStaff record);

    int changeStatus(SysStaff record);

    int updatePassword(@Param("staffId") Long staffId,
                       @Param("password") String password);

    int checkCodeRepeat(@Param("staffAccount") String staffAccount);

    SysStaff queryUserByName(@Param("staffAccount") String staffAccount);

    int lastLogin(@Param("staffAccount") String staffAccount);



}