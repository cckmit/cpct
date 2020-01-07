package com.zjtelcom.cpct.service.impl.analyst;

import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.domain.channel.Label;
import com.zjtelcom.cpct.service.analyst.AnalystService;
import com.zjtelcom.cpct.statistic.service.TrialLabelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AnalystServiceImpl implements AnalystService {

    private Logger logger = LoggerFactory.getLogger(AnalystServiceImpl.class);

    @Autowired
    private InjectionLabelMapper labelMapper;

    @Autowired(required = false)
    private TrialLabelService trialLabelService;

    @Override
    public Map<String, Object> statisticalAnalysts(HashMap<String, Object> params) {
        logger.info("service 方法 ：statisticalAnalysts 执行~");
        Map<String, Object> result =  trialLabelService.statisticalAnalysts(params);
        return result;
//        return null;
    }

    @Override
    public Map<String, Object> getCustomByLabel(String name) {
        HashMap<String, Object> hashMap = new HashMap<>();
        List<Label> sysLabel = labelMapper.selectByScopeLikeName(name);
        if (sysLabel.size()>0 && sysLabel!=null){
            hashMap.put("code","200");
            hashMap.put("msg",sysLabel);
        }else {
            ArrayList<Label> labels = new ArrayList<>();
            hashMap.put("code","500");
            hashMap.put("msg",labels);
        }
        return hashMap;
    }
}
