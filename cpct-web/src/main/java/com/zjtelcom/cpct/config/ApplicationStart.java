package com.zjtelcom.cpct.config;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import com.zjtelcom.cpct.domain.system.SysParams;
import com.zjtelcom.cpct.util.RedisUtils;
import com.zjtelcom.cpct.util.SystemParamsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(1)  // 数值约低优先级越高
public class ApplicationStart implements ApplicationRunner {


    private static final Logger log = Logger.getLogger(ApplicationStart.class);

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Override
    public void run(ApplicationArguments args) throws Exception {
         //加载系统参数到redis
        if(isTrue()) {
            List<SysParams> sysParams = sysParamsMapper.selectAll(null, null);
            log.info("初始化系统参数到redis：" + JSONObject.toJSONString(JSON.toJSON(sysParams)));
            for (SysParams params : sysParams) {
                if (StringUtils.isNotBlank(params.getParamKey())) {
                    try {
                        redisUtils.set(params.getParamKey(), params.getParamValue());
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("redis插入数据错误");
                    }
                }
            }
            log.info("系统参数缓存完毕");
        }
    }

    /**
     * 是否初始化系统参数到redis  防止本地环境启动混淆参数
     * @return
     */
    private boolean isTrue(){
        String isOpen= SystemParamsUtil.getActiveStatus();
        if(StringUtils.isNotBlank(isOpen)){
            if("pst".equals(isOpen)||"pre".equals(isOpen)||"prod".equals(isOpen)||"gray".equals(isOpen)){
                  return true;
            }
        }
        return false;
    }
}
