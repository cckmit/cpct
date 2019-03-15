package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktKeywords;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
@Mapper
@Repository
public interface MktKeywordsMapper {
    int deleteByPrimaryKey(Long keywordId);

    int insert(MktKeywords record);

    MktKeywords selectByPrimaryKey(Long keywordId);

    List<MktKeywords> selectAll();

    int updateByPrimaryKey(MktKeywords record);
}