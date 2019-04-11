package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.IndexLog;

import java.util.List;

public interface IndexLogMapper {
    int deleteByPrimaryKey(Long indexId);

    int insert(IndexLog record);

    IndexLog selectByPrimaryKey(Long indexId);

    List<IndexLog> selectAll();

    int updateByPrimaryKey(IndexLog record);
}