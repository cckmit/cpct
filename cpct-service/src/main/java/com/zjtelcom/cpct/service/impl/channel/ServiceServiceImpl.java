package com.zjtelcom.cpct.service.impl.channel;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zjtelcom.cpct.common.Page;
import com.zjtelcom.cpct.dao.channel.ServiceMapper;
import com.zjtelcom.cpct.domain.channel.ServiceEntity;
import com.zjtelcom.cpct.service.channel.ServiceService;
import com.zjtelcom.cpct.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

@Service
public class ServiceServiceImpl implements ServiceService {

    @Autowired
    private ServiceMapper serviceMapper;

    @Override
    public Map<String, Object> getServiceListByName(Map<String,Object> params) {
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
}
