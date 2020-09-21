package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.BatchSendDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface BatchSendMapper {
    int insert(BatchSendDO batchSendDO);
    List<BatchSendDO> selectByBatchNum(String batchNum);//获取对应批次记录
    int updateStateByBatchNum(String batchNum, String state);


}
