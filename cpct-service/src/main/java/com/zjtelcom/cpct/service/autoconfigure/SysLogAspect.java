package com.zjtelcom.cpct.service.autoconfigure;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ctzj.smt.bss.core.util.SpringContextUtil;
import com.google.gson.Gson;
import com.zjtelcom.annotation.SysLog;
import com.zjtelcom.cpct.dubbo.service.MqProducerService;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.UrlUtil;
import com.zjtelcom.cpct.util.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

import static com.zjtelcom.cpct.constants.CommonConstant.CODE_FAIL;
import static com.zjtelcom.cpct.constants.CommonConstant.EVENT_ACCESS_SERVICE;
import static com.zjtelcom.cpct.constants.CommonConstant.NORMAL_SERVICE;

@Aspect
@Slf4j
public class SysLogAspect {

    @Autowired(required = false)
    private MqProducerService mqProducerService;

    @Value("${ctg.cpctEsLogTopic}")
    private String cpctEsLogTopic;
    private String esType = "doc";
    @Autowired
    private RedisUtils redisUtils;

    /**
     * 1，表示在哪个类的哪个方法进行切入。配置有切入点表达式。
     * 2，对有@SystemLog标记的方法,记录其执行参数及返回结果。
     */
    @Pointcut("@annotation(com.zjtelcom.annotation.SysLog)")
    public void controllerAspect() {
    }

    /**
     * 配置controller环绕通知,使用在方法aspect()上注册的切入点
     */
    @Around("controllerAspect()")
    public Object handlerControllerMethod(ProceedingJoinPoint pjp) throws Throwable{
        if (log.isDebugEnabled()) {
//            log.info(">>>>>>>>>>>>>>>进入日志切面<<<<<<<<<<<<<<<<");
        }
        Object result = null;
        // 获取方法签名
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature)signature;
        //java reflect相关类，通过反射得到注解
        Method targetMethod = methodSignature.getMethod();

        long startTime = System.currentTimeMillis();
        //初始化log
        JSONObject esJson = new JSONObject();
        String serviceId = null;
        String indexName = null;
        String reqId = null;
        // 需要记录日志
        if(targetMethod.isAnnotationPresent(SysLog.class)) {
//            Gson gson = new Gson();
//            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

            // 获取注解
            SysLog sysLog = targetMethod.getAnnotation(SysLog.class);
            serviceId = sysLog.serviceId();
            esJson.put("actionName",sysLog.actionName());
            esJson.put("params",pjp.getArgs());
            esJson.put("moduleName",sysLog.moduleName());
            JSONObject params = (JSONObject)esJson.get("params");
            indexName = params.get("indexName").toString();
            reqId = params.get("reqId")==null ? null : params.get("reqId").toString();
        }
        try {
            result = pjp.proceed();

            String prodFilter = "0";
            prodFilter = redisUtils.getRedisOrSysParams("SYSYTEM_ESLOG_STATUS");
            if (prodFilter.equals("0")) {
                return result;
            }
            if (serviceId != null && serviceId.equals(NORMAL_SERVICE)) {
                esJson = (JSONObject) result;
            } else {
                esJson.put("result", result);
            }
        } catch (Throwable e) {
            esJson.put("exception",UrlUtil.getTrace(e));
            esJson.put("result",CODE_FAIL);
        }
        // 本次操作用时（毫秒）
        long elapsedTime = System.currentTimeMillis() - startTime;
        log.info("[{}]use time: {}", pjp.getSignature(), elapsedTime);
        esJson.put("usedTime",String.valueOf(elapsedTime));
        System.out.println(JSON.toJSONString(esJson));

        // 发送消息到 系统日志队列
        if(targetMethod.isAnnotationPresent(SysLog.class)) {
            SysLog sysLog = targetMethod.getAnnotation(SysLog.class);
            log.info(">>>>>>>>>>>>>>>切面日志结束<<<<<<<<<<<<<<<<");
            if (reqId == null) {
                mqProducerService.msg2ESLogProducer(esJson, cpctEsLogTopic,  indexName  + "," + esType, null);
            } else {
                 mqProducerService.msg2ESLogProducer(esJson, cpctEsLogTopic,  indexName  + "," + esType + "," + reqId, null);
            }
        }
        return result;
    }

    @AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint point, Throwable e) {
        try {
            handle(point, null, e);
        } catch (Exception ex) {
            log.error("日志记录出错!", ex);
        }
    }

    private void handle(JoinPoint point, Long methodProcessTime, Throwable e) throws Exception {
        //获取拦截的方法名
        Signature sig = point.getSignature();
        MethodSignature msig = null;
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException("该注解只能用于方法");
        }
        msig = (MethodSignature) sig;
        Object target = point.getTarget();
        Method currentMethod = target.getClass().getMethod(msig.getName(), msig.getParameterTypes());
        String methodName = currentMethod.getName();
        //获取拦截方法的参数
        Object[] params = point.getArgs();
        //获取操作名称
        SysLog annotation = currentMethod.getAnnotation(SysLog.class);
        String moduleName = annotation.moduleName();
    }








}
