package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Script;

import java.util.List;

public interface MktScriptMapper {
    int deleteByPrimaryKey(Long scriptId);

    int insert(Script record);

    Script selectByPrimaryKey(Long scriptId);

    List<Script> selectAll();

    int updateByPrimaryKey(Script record);
}