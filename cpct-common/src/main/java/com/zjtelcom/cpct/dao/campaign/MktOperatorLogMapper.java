package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktOperatorLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MktOperatorLogMapper {

    /*
    **活动操作记录添加
     */
    int insertOperation(MktOperatorLogDO mktOperatorLogDO);
}
