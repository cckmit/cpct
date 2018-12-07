package com.zjtelcom.cpct.service.impl.channel;

import com.zjtelcom.cpct.dao.channel.ServiceTypeMapper;
import com.zjtelcom.cpct.domain.channel.ServiceType;
import com.zjtelcom.cpct.service.channel.ServiceTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<ServiceType> serviceTypeList = serviceTypeMapper.selectAll();
        result.put("resultCode",CODE_SUCCESS);
        result.put("resultMsg",serviceTypeList);
        return result;
    }

}