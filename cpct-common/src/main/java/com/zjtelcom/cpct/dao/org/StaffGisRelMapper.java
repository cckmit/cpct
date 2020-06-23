package com.zjtelcom.cpct.dao.org;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface StaffGisRelMapper {

    List<String> selectStaffTelByGisCode(String gisCode);

    List<String> selectStaffTelByGisCodeOne(@Param("gisCode") String gisCode, @Param("postionzj") String postionzj);


}
