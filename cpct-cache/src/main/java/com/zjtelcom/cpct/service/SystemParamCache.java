package com.zjtelcom.cpct.service;



import com.zjtelcom.cpct.common.ABSGGCache;
import com.zjtelcom.cpct.dao.system.SystemParamMapper;
import com.zjtelcom.cpct.dto.system.SystemParam;
import com.zjtelcom.cpct.util.JSonUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;


/**
 * User:sunjianyuan 1063 Date:2017/4/19 Time:14:38 Todo:系统参数缓存 Copyright:南京星邺汇捷网络科技有限公司 Version:1.0
 */
public class SystemParamCache extends ABSGGCache<SystemParam> {
    private static final Logger LOG = Logger.getLogger("cache");

    public SystemParamCache(String name, long refreshTimeSeconds) {
        super(name, refreshTimeSeconds);
    }

    @Override
    public void synIt() {
        LOG.debug("SystemParamCache cache start.");
        String content = null;
        try {
            List<SystemParam> systemParams = getBean(SystemParamMapper.class).queryAllSystemParam();
            long startTime = System.currentTimeMillis();
            this.removeAll();

            if (systemParams == null || systemParams.size() == 0) {
                LOG.info("SystemParam list is empty in database.");
                return;
            }

            for (SystemParam systemParam : systemParams) {
                this.addOne(systemParam.getParamKey(), systemParam);
            }

            LOG.info("SystemParam cache end, consume " + (System.currentTimeMillis() - startTime) + " ms.");
            content = JSonUtil.switch2Str(systemParams);
        }
        catch (Exception e) {
            LOG.error("SystemParam cache" + e.getMessage(), e);
            content = "";
        }
        LOG.debug("SystemParam cache <> " + content);

    }

    @Override
    public Object missCallBack(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }

        return null;
    }
}
