package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktCamCust;
import com.zjtelcom.cpct.domain.channel.MktVerbalCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktCamCustMapper {
    int deleteByPrimaryKey(Long mktCamCustId);

    int insert(MktCamCust record);

    MktCamCust selectByPrimaryKey(Long mktCamCustId);

    List<MktCamCust> selectAll();

    List<MktCamCust> selectByTarGrpTempId(@Param("tarTempId")Long tarTempId);

    int updateByPrimaryKey(MktCamCust record);

    int insertByBatch(@Param("list") List<MktCamCust> record);
}