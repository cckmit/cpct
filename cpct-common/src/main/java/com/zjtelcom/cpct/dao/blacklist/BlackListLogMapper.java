package com.zjtelcom.cpct.dao.blacklist;


import com.zjtelcom.cpct.domain.blacklist.BlackListLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface BlackListLogMapper {
    List<BlackListLogDO> getBlackListLog(@Param("assetPhone") String assetPhone, @Param("serviceCate") String serviceCate, @Param("maketingCate") String maketingCate, @Param("publicBenefitCate") String publicBenefitCate, @Param("channel") String channel, @Param("staffId") String staffId, @Param("startDate") String startDate, @Param("endDate") String endDate, @Param("operType") String operType);

    int addBlacklistlog(BlackListLogDO blackListLogDO);

}
