package com.zjtelcom.cpct.service.impl.querySaturation;

import com.alibaba.fastjson.JSON;
import com.zjhcsoft.eagle.main.dubbo.service.QuerySaturationService;
import com.zjtelcom.cpct.dao.channel.LabelSaturationMapper;
import com.zjtelcom.cpct.domain.channel.LabelSaturation;
import com.zjtelcom.cpct.service.querySaturation.QuerySaturationCpcService;
import com.zjtelcom.cpct.util.UserUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/04/10 16:53
 * @version: V1.0
 */
@Service
@Transactional
public class QuerySaturationCpcServiceImpl implements QuerySaturationCpcService {

    private static final Logger log = LoggerFactory.getLogger(QuerySaturationCpcServiceImpl.class);

    @Autowired (required = false)
    private QuerySaturationService querySaturationService;

    @Autowired
    private LabelSaturationMapper labelSaturationMapper;

    /**
     * queryDate : "yyyy-MM-dd"
     * lanId : 非必填
     */
    @Override
    public boolean querySaturationCpc(String queryDate, String lanId) {
        boolean result = false;
        try {
            Map<String, String> map = querySaturationService.querySaturation(queryDate, lanId);
            log.info("map = " + JSON.toJSONString(map));
            List<Map<String,Object>>  labelList = (List<Map<String,Object>>) JSON.parse(map.get("labelSaturation"));
            List<LabelSaturation> labelSaturationList = new ArrayList<>();
            if(labelList !=null && labelList.size()>0){
                for (Map<String,Object> labelMap : labelList) {
                    LabelSaturation labelSaturation = new LabelSaturation();
                    labelSaturation.setLabelCode(labelMap.get("LABEL_ENG_NAME").toString());
                    labelSaturation.setBigdataSaturation(Long.valueOf(labelMap.get("LABEL_NOT_NULL_CNT").toString()));
                    labelSaturation.setSaturationBatchNumber(labelMap.get("DATE_CD").toString());
                    labelSaturation.setCreateDate(new Date());
                    labelSaturation.setUpdateDate(new Date());
                    labelSaturation.setCreateStaff(UserUtil.loginId());
                    labelSaturation.setUpdateStaff(UserUtil.loginId());
                    labelSaturationList.add(labelSaturation);
                }
                labelSaturationMapper.insertBatch(labelSaturationList);
            }
            result = true;
        } catch (Exception e) {
            log.error("标签饱和度接口查询失败，Exception = " + e);
            result = false;
        }
        return result;
    }
}