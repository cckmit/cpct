package com.zjtelcom.cpct.service.impl.synchronize;

import com.zjtelcom.cpct.dao.synchronize.SynchronizeRecordMapper;
import com.zjtelcom.cpct.dto.synchronize.SynchronizeRecord;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Auther: anson
 * @Date: 2018/8/27
 * @Description:
 */
@Service
public class SynchronizeRecordServiceImpl implements SynchronizeRecordService {

     @Autowired
     private SynchronizeRecordMapper synchronizeRecordMapper;

    /**
     * 新增同步记录
     * @param record
     * @return
     */
    @Override
    public int insert(SynchronizeRecord record) {
        return synchronizeRecordMapper.insert(record);
    }
}
