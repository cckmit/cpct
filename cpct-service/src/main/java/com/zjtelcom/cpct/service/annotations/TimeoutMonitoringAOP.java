package com.zjtelcom.cpct.service.annotations;

import com.zjtelcom.cpct.service.dubbo.UCCPService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class TimeoutMonitoringAOP {

    @Autowired
    private UCCPService uccpService;

    final String TimeoutMonitoring = "SYSTEM_TIMEOUT_MONITORING";

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
        map = null;
    }

    @Pointcut("@annotation(com.zjtelcom.cpct.service.annotations.InterfaceTimeoutMonitoring)")
    public void pointcut() {}

    @Before("@annotation(interfaceTimeoutMonitoring)")
    public void startMonitoring(JoinPoint joinPoint, InterfaceTimeoutMonitoring interfaceTimeoutMonitoring){
        start = System.currentTimeMillis();
    }

    @AfterReturning("@annotation(interfaceTimeoutMonitoring)")
    public void endMonitoring(ProceedingJoinPoint joinPoint, InterfaceTimeoutMonitoring interfaceTimeoutMonitoring) {
        long end = System.currentTimeMillis();
        long time = end - start;
        String name = joinPoint.getSignature().getName();
        // key为当前切入的方法名
        if (interfaceTimeoutMonitoring.timeout() < time && map.get(name) == null ? true : (map.get(name) < 3 ? true : false)) {
            try {
                String sendContent = name + "方法调用超时，请前往查看！";
                String s = uccpService.sendShortMessage("15355003610", sendContent, "571");
                if (null != null && !"".equals(s)) {
                    map.put(name, map.get(name) == null ? 1 : (map.get(name) + 1));
                }
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
