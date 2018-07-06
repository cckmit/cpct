package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktVerbal;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MktVerbalMapper {
    int deleteByPrimaryKey(Long verbalId);

    int insert(MktVerbal record);

    MktVerbal selectByPrimaryKey(Long verbalId);

    List<MktVerbal> findVerbalListByConfId(@Param("verbalId") Long verbalId);

    List<MktVerbal> selectAll();

    int updateByPrimaryKey(MktVerbal record);
}