package com.zjtelcom.cpct.dao.synchronize;

import com.zjtelcom.cpct.domain.event.EventDO;
import com.zjtelcom.cpct.dto.synchronize.SynchronizeRecord;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:同步操作记录
 */
@Mapper
@Repository
public interface SynchronizeRecordMapper {

    int insert(SynchronizeRecord record);
}
