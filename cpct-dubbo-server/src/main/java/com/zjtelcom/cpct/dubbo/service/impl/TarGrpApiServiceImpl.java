package com.zjtelcom.cpct.dubbo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.ExpressRunner;
import com.ql.util.express.Operator;
import com.ql.util.express.rule.RuleResult;
import com.zjpii.biz.serv.YzServ;
import com.zjtelcom.cpct.dao.channel.InjectionLabelMapper;
import com.zjtelcom.cpct.dao.filter.FilterRuleMapper;
import com.zjtelcom.cpct.dao.grouping.TarGrpConditionMapper;
import com.zjtelcom.cpct.domain.channel.LabelResult;
import com.zjtelcom.cpct.dto.filter.FilterRule;
import com.zjtelcom.cpct.dubbo.service.TarGrpApiService;

import com.zjtelcom.cpct.elastic.config.IndexList;
import com.zjtelcom.cpct.elastic.service.EsHitService;
import com.zjtelcom.cpct.service.api.TarGrpCheckService;
import com.zjtelcom.cpct.service.es.EsHitsService;
import com.zjtelcom.cpct.util.ChannelUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/04/22 14:11
 * @version: V1.0
 */
@Service
public class TarGrpApiServiceImpl implements TarGrpApiService {

    @Autowired
    private TarGrpCheckService tarGrpCheckService;

    /**
     * 获取命中的客户分群
     *
     * @param paramsMap
     * @return
     */
    @Override
    public Map<String, Object> getCpcTargrp(Map<String, Object> paramsMap) {
        return tarGrpCheckService.cpcTarGrpCheck(paramsMap);
    }

}