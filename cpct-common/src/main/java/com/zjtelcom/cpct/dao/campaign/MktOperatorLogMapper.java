package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktOperatorLogDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktOperatorLogMapper {

    /*
    **活动操作记录添加
     */
    int insertOperation(MktOperatorLogDO mktOperatorLogDO);

    /*
    **活动操作记录查找
     */
    List<MktOperatorLogDO> selectByPrimaryKey(MktOperatorLogDO mktOperatorLogDO);
}
