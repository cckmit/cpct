package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.IntfCfgParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface IntfCfgParamMapper {
    int deleteByPrimaryKey(Long intfCfgParamId);

    int insert(IntfCfgParam record);

    IntfCfgParam selectByPrimaryKey(Long intfCfgParamId);

    List<IntfCfgParam> selectAll();

    int updateByPrimaryKey(IntfCfgParam record);
}