package com.zjtelcom.cpct.dao.channel;


import com.zjtelcom.cpct.domain.channel.MktVerbal;

import java.util.List;

public interface MktVerbalMapper {
    int deleteByPrimaryKey(Long verbalId);

    int insert(MktVerbal record);

    MktVerbal selectByPrimaryKey(Long verbalId);

    List<MktVerbal> selectAll();

    int updateByPrimaryKey(MktVerbal record);
}