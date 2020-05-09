package com.zjtelcom.cpct.dao.org;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface StaffGisRelMapper {

    String selectStaffTelByGisCode(String gisCode);


}
