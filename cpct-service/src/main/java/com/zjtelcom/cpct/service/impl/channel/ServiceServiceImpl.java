package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.ServiceMapper;
import com.zjtelcom.cpct.domain.channel.ServiceEntity;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.BaseService;
import com.zjtelcom.cpct.service.channel.ServiceService;
import com.zjtelcom.cpct.service.synchronize.channel.SynServiceService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.MapUtil;
import com.zjtelcom.cpct.util.SystemParamsUtil;
import com.zjtelcom.cpct.util.UserUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ServiceServiceImpl extends BaseService implements ServiceService {

    @Autowired
    private ServiceMapper serviceMapper;
    @Autowired
    private SynServiceService synServiceService;

    @Value("${sync.value}")
    private String value;

    @Override
    public Map<String, Object> getServiceList(Long userId, Map<String,Object> params) {
        Map<String, Object> result = new HashMap<>();
        ServiceEntity serviceEntity = new ServiceEntity();
        String serviceName = MapUtil.getString(params.get("serviceName"));
        String serviceTypeId = MapUtil.getString(params.get("serviceTypeId"));
        if(StringUtils.isNotBlank(serviceName)) {
            serviceEntity.setServiceName(serviceName);
        }
        if(StringUtils.isNotBlank(serviceTypeId)) {
            serviceEntity.setServiceTypeId(Long.valueOf(serviceTypeId));
        }
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        PageHelper.startPage(page, pageSize);
        List<ServiceEntity> serviceEntityList = serviceMapper.selectDetailByServiceEntity(serviceEntity);
        Page pageInfo = new Page(new PageInfo(serviceEntityList));
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", serviceEntityList);
        result.put("page",pageInfo);
        return result;
    }


    @Override
    public Map<String, Object> getServiceListByName(Long userId, Map<String,Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<ServiceEntity> serviceEntityList = new ArrayList<>();
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        PageHelper.startPage(page, pageSize);

        if (params.get("serviceName") != null) {
            String serviceName = params.get("serviceName").toString();
            serviceEntityList = serviceMapper.selectByServiceName(serviceName);
        } else {
            serviceEntityList = serviceMapper.selectAll();
        }
        List<ServiceEntity> serviceEntities = new ArrayList<>();
        List<Long> serviceIdList = (List<Long>) params.get("serviceIdList");
        if (serviceEntityList != null && serviceIdList != null) {
            for (ServiceEntity serviceEntity : serviceEntityList) {
                if (!serviceIdList.contains((serviceEntity.getServiceId()))) {
                    serviceEntities.add(serviceEntity);
                }
            }
        }

        Page pageInfo = new Page(new PageInfo(serviceEntityList));
        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", serviceEntities);
        result.put("page", pageInfo);
        return result;
    }

    @Override
    public Map<String, Object> createService(Long userId, ServiceEntity addVO) {
        Map<String, Object> result = new HashMap<>();
        final ServiceEntity serviceEntity = BeanUtil.create(addVO, new ServiceEntity());
        serviceEntity.setCreateStaff(UserUtil.loginId());
        serviceEntity.setCreateDate(new Date());
        serviceEntity.setUpdateStaff(UserUtil.loginId());
        serviceEntity.setUpdateDate(new Date());
        serviceEntity.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        serviceEntity.setStatusDate(new Date());
        serviceMapper.insert(serviceEntity);

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synServiceService.synchronizeSingleService(serviceEntity.getServiceId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String, Object> modService(Long userId, ServiceEntity editVO) {
        Map<String, Object> result = new HashMap<>();
        final ServiceEntity serviceEntity = serviceMapper.selectByPrimaryKey(editVO.getServiceId());
        if(serviceEntity == null) {
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","服务信息不存在");
            return result;
        }
        BeanUtil.copy(editVO, serviceEntity);
        serviceEntity.setUpdateDate(new Date());
        serviceEntity.setUpdateStaff(UserUtil.loginId());
        serviceMapper.updateByPrimaryKey(serviceEntity);

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synServiceService.synchronizeSingleService(serviceEntity.getServiceId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","修改成功");
        return result;
    }

    @Override
    public Map<String, Object> delService(Long userId, ServiceEntity delVO) {
        Map<String, Object> result = new HashMap<>();
        final ServiceEntity serviceEntity = serviceMapper.selectByPrimaryKey(delVO.getServiceId());
        if(serviceEntity == null) {
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","服务信息不存在");
            return result;
        }
        serviceMapper.deleteByPrimaryKey(delVO.getServiceId());

        if (SystemParamsUtil.isSync()){
            new Thread(){
                public void run(){
                    try {
                        synServiceService.deleteSingleService(serviceEntity.getServiceId(),"");
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }

    @Override
    public Map<String, Object> getServiceDetail(Long userId, Long serviceId) {
        Map<String, Object> result = new HashMap<>();
        ServiceEntity serviceEntity = new ServiceEntity();
        try{
            serviceEntity = serviceMapper.selectByPrimaryKey(serviceId);
        }catch (Exception e){
            e.printStackTrace();
            logger.error("[op:ServiceServiceImpl] fail to getServiceDetail ", e);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",serviceEntity);
        return result;
    }
}
