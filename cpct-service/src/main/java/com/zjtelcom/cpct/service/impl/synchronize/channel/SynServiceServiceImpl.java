package com.zjtelcom.cpct.service.impl.synchronize.channel;

import com.zjtelcom.cpct.constants.CommonConstant;
import com.zjtelcom.cpct.dao.channel.ServiceMapper;
import com.zjtelcom.cpct.domain.channel.ServiceEntity;
import com.zjtelcom.cpct.enums.SynchronizeType;
import com.zjtelcom.cpct.exception.SystemException;
import com.zjtelcom.cpct.service.synchronize.SynchronizeRecordService;
import com.zjtelcom.cpct.service.synchronize.channel.SynServiceService;
import com.zjtelcom.cpct_prd.dao.channel.ServicePrdMapper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class SynServiceServiceImpl implements SynServiceService {

    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private ServicePrdMapper servicePrdMapper;
    @Autowired
    private SynchronizeRecordService synchronizeRecordService;

    //同步表名
    private static final String tableName="service";

    /**
     * 单个服务同步
     * @param serviceId
     * @param roleName
     * @return
     */
    @Override
    public Map<String,Object> synchronizeSingleService(Long serviceId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        ServiceEntity serviceEntity = serviceMapper.selectByPrimaryKey(serviceId);
        if(null == serviceEntity) {
            throw new SystemException("对应服务信息不存在!");
        }
        ServiceEntity service = servicePrdMapper.selectByPrimaryKey(serviceId);
        if(null == service) {
            servicePrdMapper.insert(serviceEntity);
            synchronizeRecordService.addRecord(roleName,tableName,serviceId, SynchronizeType.add.getType());
        }else {
            servicePrdMapper.updateByPrimaryKey(serviceEntity);
            synchronizeRecordService.addRecord(roleName,tableName,serviceId, SynchronizeType.update.getType());
        }
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", StringUtils.EMPTY);
        return maps;
    }

    @Override
    public Map<String,Object> deleteSingleService(Long serviceId, String roleName) {
        Map<String,Object> maps = new HashMap<>();
        servicePrdMapper.deleteByPrimaryKey(serviceId);
        maps.put("resultCode", CommonConstant.CODE_SUCCESS);
        maps.put("resultMsg", org.apache.commons.lang.StringUtils.EMPTY);
        synchronizeRecordService.addRecord(roleName,tableName,serviceId, SynchronizeType.delete.getType());
        return maps;
    }

}
