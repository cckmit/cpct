package com.zjtelcom.cpct.service.annotations;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.deploy.panel.ITreeNode;
import com.zjtelcom.cpct.service.dubbo.UCCPService;
import com.zjtelcom.cpct.service.system.SysParamsService;
import com.zjtelcom.cpct.util.DateUtil;
import com.zjtelcom.cpct.util.RedisUtils;
import org.apache.poi.hssf.record.cf.Threshold;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class TimeoutMonitoringAOP {

    @Autowired
    private UCCPService uccpService;
    @Autowired
    private SysParamsService sysParamsService;
    @Autowired
    private RedisUtils redisUtils;

    // 系统接口超时监控
    final String timeOutMonitoring = "SYSTEM_TIMEOUT_MONITORING";
    // 接口超时阀值
    final String timeOutThreshold = "TIMEOUT_THRESHOLD";
    // 告警短信发送次数阀值
    final String msgSendThreshold = "WARNINGMSG_SENDT_THRESHOLD";
    // 告警短信发送人
    final String msgRecipient = "WARNING_MSG_RECIPIENT";

    /**
     * map全局
     *  key为方法名称
     *  value为短信发送次数
     *  当同一个方法当天短信发送超过3次时不再发送短信提醒
     */
    Map<String, Integer> map = new HashMap();

    long start = 0L;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledReset() {
        redisUtils.del(timeOutMonitoring);
    }

    @Pointcut("@annotation(com.zjtelcom.cpct.service.annotations.InterfaceTimeoutMonitoring)")
    public void pointcut() {}

    @Before("@annotation(interfaceTimeoutMonitoring)")
    public void startMonitoring(JoinPoint joinPoint, InterfaceTimeoutMonitoring interfaceTimeoutMonitoring){
        start = System.currentTimeMillis();
    }

    @AfterReturning("@annotation(interfaceTimeoutMonitoring)")
    public void endMonitoring(ProceedingJoinPoint joinPoint, InterfaceTimeoutMonitoring interfaceTimeoutMonitoring) {
        Integer x = 0;
        String name = joinPoint.getSignature().getName();
        // 告警信息存入redis中
        String value = redisUtils.hget(timeOutMonitoring, name).toString();
        if (value != null) {
            x = Integer.valueOf(value);
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        String timeOut = redisUtils.getRedisOrSysParams(timeOutThreshold);
        long l = Long.valueOf(timeOut) * 1000;
        String msgSend = redisUtils.getRedisOrSysParams(msgSendThreshold);
        Integer i = Integer.valueOf(msgSend);
        // key为当前切入的方法名
        if (l < time && x == null ? true : (x < i ? true : false)) {
            try {
                String sendContent = name + "方法在" + DateUtil.date2StringDate(new Date()) + "调用超时，用时" + time + "毫秒，请前往查看！";
                String recipient = redisUtils.getRedisOrSysParams(msgRecipient);
                JSONArray jsonArray = JSONArray.parseArray(recipient);
                for (Object array : jsonArray) {
                    JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(array));
                    String s = uccpService.sendShortMessage(jsonObject.get("phone").toString(), sendContent, jsonObject.get("lanId").toString());
                    if (null != null && !"".equals(s)) {
                        redisUtils.hset(timeOutMonitoring, name, x == null ? 1 : x + 1);
                    }
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
