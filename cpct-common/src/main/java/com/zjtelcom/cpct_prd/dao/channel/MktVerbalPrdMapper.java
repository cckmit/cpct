package com.zjtelcom.cpct_prd.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktVerbal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktVerbalPrdMapper {
    int deleteByPrimaryKey(Long verbalId);

    int deleteByConfId(Long contactConfId);

    int insert(MktVerbal record);

    MktVerbal selectByPrimaryKey(Long verbalId);

    List<MktVerbal> findVerbalListByConfId(@Param("confId") Long confId);

    List<MktVerbal> selectAll();

    int updateByPrimaryKey(MktVerbal record);
}