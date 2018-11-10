package com.zjtelcom.cpct.dao.channel;

import com.zjtelcom.cpct.domain.channel.Script;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface MktScriptMapper {
    int deleteByPrimaryKey(Long scriptId);

    int insert(Script record);

    Script selectByPrimaryKey(Long scriptId);

    List<Script> selectAll(@Param("scriptName")String scriptName, @Param("createTime")Date createTime,@Param("updateTime")Date updateTime,@Param("scriptType")String scriptType);

    List<Script> findByScriptName(@Param("scriptName")String scriptName, @Param("scriptType")String scriptType);

    int updateByPrimaryKey(Script record);
}