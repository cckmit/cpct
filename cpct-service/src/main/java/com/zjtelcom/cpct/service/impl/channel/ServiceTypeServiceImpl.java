package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.ServiceTypeMapper;
import com.zjtelcom.cpct.domain.channel.ServiceType;
import com.zjtelcom.cpct.domain.channel.ServiceTypeTree;
import com.zjtelcom.cpct.enums.StatusCode;
import com.zjtelcom.cpct.service.channel.ServiceTypeService;
import com.zjtelcom.cpct.util.BeanUtil;
import com.zjtelcom.cpct.util.MapUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
@Transactional
public class ServiceTypeServiceImpl implements ServiceTypeService {

    @Autowired
    private ServiceTypeMapper serviceTypeMapper;

    @Override
    public Map<String, Object> getServiceTypeList() {
        Map<String, Object> result = new HashMap<>();
        List<ServiceTypeTree> serviceTypeTrees = new ArrayList<>();
        List<ServiceType> parentList = serviceTypeMapper.findServiceTypeListByPar(0L);
        for(ServiceType parent : parentList) {
            ServiceTypeTree onceTree = BeanUtil.create(parent, new ServiceTypeTree());
            List<ServiceType> firstList = serviceTypeMapper.findServiceTypeListByPar(parent.getServiceTypeId());
            List<ServiceTypeTree> firstTreeList = new ArrayList<>();

            for(ServiceType first : firstList) {
                ServiceTypeTree twiceTree = BeanUtil.create(first, new ServiceTypeTree());
                List<ServiceType> twiceList = serviceTypeMapper.findServiceTypeListByPar(first.getServiceTypeId());
                List<ServiceTypeTree> twiceTreeList = new ArrayList<>();

                for(ServiceType second : twiceList) {
                    ServiceTypeTree thirdTree = BeanUtil.create(second, new ServiceTypeTree());
                    twiceTreeList.add(thirdTree);
                }
                twiceTree.setChildren(twiceTreeList);
                firstTreeList.add(twiceTree);
            }
            onceTree.setChildren(firstTreeList);
            serviceTypeTrees.add(onceTree);
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",serviceTypeTrees);
        return result;
    }

    @Override
    public Map<String, Object> getAllServiceTypeList(Map<String,Object> params) {
        Map<String, Object> result = new HashMap<>();
        String serviceTypeName = MapUtil.getString(params.get("serviceTypeName"));
        List<ServiceType> rootList = new ArrayList<>();
        List<ServiceTypeTree> serviceTypeTrees = new ArrayList<>();
        ServiceType serviceType = new ServiceType();
        serviceType.setServiceTypeName(serviceTypeName);
        serviceType.setServiceTypeName("服务类型");
        serviceType.setServiceTypeId(0L);
        rootList.add(serviceType);
        for (ServiceType root : rootList) {
            ServiceTypeTree parentTree = BeanUtil.create(root, new ServiceTypeTree());
            List<ServiceType> parentList = serviceTypeMapper.findServiceTypeListByPar(0L);
            List<ServiceTypeTree> parentTreeList = new ArrayList<>();

            for (ServiceType parent : parentList) {
                if (null != serviceTypeName && !serviceTypeName.equals("") && !parent.getServiceTypeName().contains(serviceTypeName)) {
                    continue;
                }
                ServiceTypeTree onceTree = BeanUtil.create(parent, new ServiceTypeTree());
                List<ServiceType> firstList = serviceTypeMapper.findServiceTypeListByPar(parent.getServiceTypeId());
                List<ServiceTypeTree> firstTreeList = new ArrayList<>();

                for (ServiceType first : firstList) {
                    if (!first.getServiceTypeName().contains(serviceTypeName)) {
                        continue;
                    }
                    ServiceTypeTree twiceTree = BeanUtil.create(first, new ServiceTypeTree());
                    List<ServiceType> twiceList = serviceTypeMapper.findServiceTypeListByPar(first.getServiceTypeId());
                    List<ServiceTypeTree> twiceTreeList = new ArrayList<>();

                    for (ServiceType second : twiceList) {
                        if (!second.getServiceTypeName().contains(serviceTypeName)) {
                            continue;
                        }
                        ServiceTypeTree thirdTree = BeanUtil.create(second, new ServiceTypeTree());
                        twiceTreeList.add(thirdTree);
                    }
                    twiceTree.setChildren(twiceTreeList);
                    firstTreeList.add(twiceTree);
                }
                onceTree.setChildren(firstTreeList);
                parentTreeList.add(onceTree);
            }
            parentTree.setChildren(parentTreeList);
            serviceTypeTrees.add(parentTree);
        }

        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",serviceTypeTrees);
        return result;
    }

    @Override
    public Map<String, Object> getServiceTypeByCondition(Long userId, Map<String,Object> params) {
        Map<String, Object> result = new HashMap<>();
        ServiceType serviceType = new ServiceType();
        String serviceTypeName = MapUtil.getString(params.get("serviceTypeName"));;
        if(StringUtils.isNotBlank(serviceTypeName)){
            serviceType.setServiceTypeName(serviceTypeName);
        }
        Integer page = MapUtil.getIntNum(params.get("page"));
        Integer pageSize = MapUtil.getIntNum(params.get("pageSize"));
        PageHelper.startPage(page, pageSize);
        List<ServiceType> serviceTypeList = serviceTypeMapper.getServiceTypeByConditon(serviceType);
        Page pageInfo = new Page(new PageInfo(serviceTypeList));

        result.put("resultCode", CODE_SUCCESS);
        result.put("resultMsg", serviceTypeList);
        result.put("page",pageInfo);
        return result;
    }

    @Override
    public Map<String, Object> createServiceType(Long userId, ServiceType addVO) {
        Map<String, Object> result = new HashMap<>();
        ServiceType serviceType = BeanUtil.create(addVO, new ServiceType());
        serviceType.setCreateStaff(userId);
        serviceType.setCreateDate(new Date());
        serviceType.setUpdateStaff(userId);
        serviceType.setUpdateDate(new Date());
        serviceType.setStatusCd(StatusCode.STATUS_CODE_EFFECTIVE.getStatusCode());
        serviceType.setStatusDate(new Date());
        serviceTypeMapper.insert(serviceType);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","添加成功");
        return result;
    }

    @Override
    public Map<String, Object> modServiceType(Long userId, ServiceType editVO) {
        Map<String, Object> result = new HashMap<>();
        ServiceType serviceType = serviceTypeMapper.selectByPrimaryKey(editVO.getServiceTypeId());
        if(serviceType == null) {
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","服务类型不存在");
            return result;
        }
        BeanUtil.copy(editVO, serviceType);
        serviceType.setUpdateDate(new Date());
        serviceType.setUpdateStaff(userId);
        serviceTypeMapper.updateByPrimaryKey(serviceType);
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","修改成功");
        return result;
    }

    @Override
    public Map<String, Object> delServiceType(Long userId, ServiceType serviceEntity) {
        Map<String, Object> result = new HashMap<>();
        ServiceType serviceType = serviceTypeMapper.selectByPrimaryKey(serviceEntity.getServiceTypeId());
        if(serviceType == null) {
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","服务类型不存在");
            return result;
        }
        serviceTypeMapper.deleteByPrimaryKey(serviceEntity.getServiceTypeId());
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg","删除成功");
        return result;
    }

    @Override
    public Map<String, Object> getServiceTypeDetail(Long userId, Long serviceTypeId) {
        Map<String, Object> result = new HashMap<>();
        ServiceType serviceType = serviceTypeMapper.selectByPrimaryKey(serviceTypeId);
        if(serviceType == null) {
            result.put("resultCode",CODE_FAIL);
            result.put("resultMsg","服务类型不存在");
            return result;
        }
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",serviceType);
        return result;
    }

}