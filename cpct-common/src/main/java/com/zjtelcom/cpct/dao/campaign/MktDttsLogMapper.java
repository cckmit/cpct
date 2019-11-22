package com.zjtelcom.cpct.dao.campaign;

import com.zjtelcom.cpct.domain.campaign.MktDttsLog;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktDttsLogMapper {

    int insert(MktDttsLog mktDttsLog);

    int updateByPrimaryKey(MktDttsLog mktDttsLog);

    MktDttsLog selectByPrimaryKey(Long dttsLogId);

    List<MktDttsLog> selectByCondition(MktDttsLog mktDttsLog);
}
