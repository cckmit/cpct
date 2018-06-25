package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Script;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface MktScriptMapper {
    int deleteByPrimaryKey(Long scriptId);

    int insert(Script record);

    Script selectByPrimaryKey(Long scriptId);

    List<Script> selectAll(@Param("scriptName")String scriptName, @Param("createTime")Date createTime,@Param("updateTime")Date updateTime);

    int updateByPrimaryKey(Script record);
}