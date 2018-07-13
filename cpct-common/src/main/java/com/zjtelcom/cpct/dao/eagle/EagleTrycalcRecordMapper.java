package com.zjtelcom.cpct.dao.eagle;


import com.zjtelcom.cpct.model.EagleTrycalcRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface EagleTrycalcRecordMapper {
    int deleteByPrimaryKey(Integer recordId);

    int insert(EagleTrycalcRecord record);

    int insertSelective(EagleTrycalcRecord record);

    EagleTrycalcRecord selectByPrimaryKey(Integer recordId);

    int updateByPrimaryKeySelective(EagleTrycalcRecord record);

    int updateByPrimaryKey(EagleTrycalcRecord record);

    List<EagleTrycalcRecord> queryByActivityId(@Param("activityId")
                                                       String activityId, @Param("status")
                                                       String status);

    List<EagleTrycalcRecord> queryByActivityIdAndPolicyId(@Param("activityId")
                                                                  String activityId, @Param("policyId")
                                                                  String policyId, @Param("status")
                                                                  String status);

    List<EagleTrycalcRecord> queryByActivityIdAndPolicyIdAndSerial(@Param("activityId")
                                                                           String activityId, @Param("policyId") String policyId, @Param("status") String status,
                                                                   @Param("serialNum") String serialNum);

    List<EagleTrycalcRecord> queryBySerialNum(@Param("activityId")
                                                      String activityId, @Param("policyId")
                                                      String policyId, @Param("status")
                                                      String status, @Param("serialNum")
                                                      String serialNum);
}