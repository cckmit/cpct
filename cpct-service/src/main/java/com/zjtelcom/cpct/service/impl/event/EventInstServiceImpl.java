
package com.zjtelcom.cpct.service.impl.event;

import com.alibaba.fastjson.JSON;
import com.ctzj.smt.bss.cooperate.service.dubbo.ICpcAPIService;
import com.zjtelcom.cpct.service.event.EventInstService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.CODE_SUCCESS;

/**
 * @Description:
 * @author: linchao
 * @date: 2019/11/14 14:17
 * @version: V1.0
 */
@Service
@Transactional
public class EventInstServiceImpl implements EventInstService {

    @Autowired(required = false)
    private ICpcAPIService iCpcAPIService;

    @Override
    public Map<String, Object> queryEventInst(Map<String, String> paramsMap) {

        System.out.println("queryEventInst接口入参：" + JSON.toJSONString(paramsMap));
        // 调用协同中心的queryEventInst接口
        Map<String, Object>  resultMap = iCpcAPIService.queryEventInst(paramsMap);
        System.out.println("queryEventInst接口出参：" + JSON.toJSONString(resultMap));
        if("1".equals(resultMap.get("resultCode"))){
            resultMap.put("resultCode", CODE_SUCCESS);
        } else {
            resultMap.put("resultCode", CODE_FAIL);
        }
        return resultMap;
    }

    @Override
    public Map<String, Object> queryEventInstLog(Map<String, String> paramsMap) {

        System.out.println("queryEventInstLog接口入参：" + JSON.toJSONString(paramsMap));
        // 调用协同中心的queryEventInstLog接口
        Map<String, Object>  resultMap = iCpcAPIService.queryEventInstLog(paramsMap);
        System.out.println("queryEventInstLog接口出参：" + JSON.toJSONString(resultMap));
        if("1".equals(resultMap.get("resultCode"))){
            resultMap.put("resultCode", CODE_SUCCESS);
        } else {
            resultMap.put("resultCode", CODE_FAIL);
        }
        return resultMap;
    }


}