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

    /**
     * 新增同并记录
     * @param roleName  角色
     * @param name      同步表名称
     * @param eventId   同步主键
     * @param type      操作类型
     * @return
     */
    @Override
    public int addRecord(String roleName, String name,Long eventId, Integer type) {
        SynchronizeRecord synchronizeRecord=new SynchronizeRecord();
        synchronizeRecord.setRoleName(roleName);
        synchronizeRecord.setSynchronizeName(name);
        synchronizeRecord.setSynchronizeType(type);
        synchronizeRecord.setSynchronizeId(eventId.toString());
        return  synchronizeRecordMapper.insert(synchronizeRecord);
    }


}
