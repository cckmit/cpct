package com.zjtelcom.cpct.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Mapper
@Repository
public interface MqLogMapper {

    int insertSendLog(@Param("msgId") String msgId, @Param("ruleId")String ruleId, @Param("batchNum")String batchNum);

    @Insert("INSERT INTO consumer_log (MSG_ID,RULE_ID,BATCHNUM,CREATE_DATE,FLAG) VALUES (#{messageID},#{ruleId},#{batchNum},now(),'2')")
    int insertSendLog2(@Param("messageID")String messageID, @Param("ruleId")String ruleId, @Param("batchNum")String batchNum);

}
