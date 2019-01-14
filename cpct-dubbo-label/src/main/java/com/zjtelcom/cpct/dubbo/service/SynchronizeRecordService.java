package com.zjtelcom.cpct.dubbo.service;

import com.zjtelcom.cpct.dto.synchronize.SynchronizeRecord;

import java.util.Map;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:同步记录
 */
public interface SynchronizeRecordService {

    int insert(SynchronizeRecord record);


    int addRecord(String roleName, String name, Long eventId, Integer type);


    Map<String,Object> selectRecordList(Map<String, Object> params);



}
