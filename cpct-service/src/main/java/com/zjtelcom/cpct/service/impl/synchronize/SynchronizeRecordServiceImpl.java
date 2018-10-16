package com.zjtelcom.cpct.service.impl.synchronize;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.synchronize.SynchronizeRecordMapper;
import com.zjtelcom.cpct.dto.synchronize.SynchronizeRecord;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    /**
     * 查询同步操作日志
     * @param params
     * @return
     */
    @Override
    public Map<String, Object> selectRecordList(Map<String, Object> params) {
        Map<String, Object> maps = new HashMap<>();
        String type= (String) params.get("type");
        int page= (Integer) params.get("page");
        int pageSize= (Integer) params.get("pageSize");
        String startTime=(String) params.get("startTime");
        String endTime=(String) params.get("endTime");
        Integer typeId=null;
        if(StringUtils.isNotBlank(type)){
            typeId=Integer.parseInt(type);
        }



        boolean tip=false;
        if(page!=0) {
            PageHelper.startPage(page, pageSize);
            tip=true;
        }
        List<SynchronizeRecord> list=new ArrayList<>();
        SynchronizeRecord synchronizeRecord=new SynchronizeRecord();
        synchronizeRecord.setSynchronizeType(typeId);
        synchronizeRecord.setStartTime(startTime);
        synchronizeRecord.setEndTime(endTime);
        list=synchronizeRecordMapper.selectList(synchronizeRecord);
        Page pageInfo = new Page(new PageInfo(list));
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg",list);
        if(tip){
            maps.put("page",pageInfo);
        }
        return maps;
    }


}
