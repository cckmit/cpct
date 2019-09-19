package com.zjtelcom.cpct.service.impl.channel;

import com.alibaba.fastjson.JSON;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.grouping.TrialOperation;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.service.api.TestService;
import com.zjtelcom.cpct.service.impl.grouping.TrialOperationServiceImpl;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.SpringUtil;
import com.zjtelcom.es.es.entity.TrialOperationVOES;
import com.zjtelcom.es.es.entity.model.TrialResponseES;
import com.zjtelcom.es.es.service.EsService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Transactional
public class TestServiceImpl implements TestService {
    @Autowired
    private SysParamsMapper sysParamsMapper;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired(required = false)
    private EsService esService;
    ExecutorService executorService = Executors.newFixedThreadPool(100);

    @Override
    public Map<String, Object> caculateTest() {
        Map<String,Object> map = new HashMap<>();
        TrialOperationVOES trialOperation = new TrialOperationVOES();
        List<SysParams> sysParams = sysParamsMapper.listParamsByKeyForCampaign("THREAD_NUM");
        String paramValue = sysParams.get(0).getParamValue();
        trialOperation.setBatchNum(Long.valueOf(paramValue));
        String value = sysParams.get(1).getParamValue();
        System.out.println("当前启动线程数："+value);
        try {
            Future<Map<String, Object>> future = executorService.submit(new testTask(trialOperation));
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        map.put("code","200");
        map.put("msg","成功");
        return map;
    }

    class testTask implements Callable<Map<String,Object>> {
        private   TrialOperationVOES trialOperation;
        public testTask(TrialOperationVOES trialOperation){
            this.trialOperation = trialOperation;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String,Object> map = new HashMap<>();
            TrialResponseES responseES = null;
            try {
                Long time = System.currentTimeMillis();
                responseES = esService.trialLog(trialOperation);
                Long num = System.currentTimeMillis()- time;
                System.out.println("*****************调用总耗时："+num);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("调用失败返回");
            }
            System.out.println("调用成功返回："+JSON.toJSONString(responseES));
            return map;
        }
    }

    @Test
    public void  test2(){
        RedisUtils bean = SpringUtil.getBean(RedisUtils.class);
        String s = (String) bean.get("Event_" + 1L);
        System.out.println(s);
    }

    @Test
    public void  test(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        List<Future<Map<String,Object>>> futureList = new ArrayList<>();
        for (Long i=1L;i<10L;i++){
             Future<Map<String,Object>> future = executorService.submit(new testTa(i));
             futureList.add(future);
        }
        try {
            for ( Future<Map<String,Object>> future : futureList){
                Map<String,Object> result = null;
                try {
                    result = future.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                System.out.println(JSON.toJSONString(result));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class testTa implements Callable<Map<String,Object>> {
        private Long num;
        public testTa(Long num){
            this.num = num;
        }

        @Override
        public Map<String, Object> call() throws Exception {
            Map<String,Object> map = new HashMap<>();
            map.put("result",num/(num-1));
            System.out.println("调用成功返回："+JSON.toJSONString(num));
            return map;
        }
    }
}
