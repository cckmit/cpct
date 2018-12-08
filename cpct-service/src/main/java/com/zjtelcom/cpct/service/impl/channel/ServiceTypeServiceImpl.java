package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.channel.ServiceTypeMapper;
import com.zjtelcom.cpct.domain.channel.ServiceType;
import com.zjtelcom.cpct.domain.channel.ServiceTypeTree;
import com.zjtelcom.cpct.service.channel.ServiceTypeService;
import com.zjtelcom.cpct.util.BeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
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

}