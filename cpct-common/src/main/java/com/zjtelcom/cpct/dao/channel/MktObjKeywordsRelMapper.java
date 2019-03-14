package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktObjKeywordsRel;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktObjKeywordsRelMapper {
    int deleteByPrimaryKey(Long relId);

    int insert(MktObjKeywordsRel record);

    MktObjKeywordsRel selectByPrimaryKey(Long relId);

    List<MktObjKeywordsRel> selectAll();

    int updateByPrimaryKey(MktObjKeywordsRel record);
}