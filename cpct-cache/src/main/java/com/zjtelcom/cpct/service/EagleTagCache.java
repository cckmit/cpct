package com.zjtelcom.cpct.service;

import com.zjtelcom.cpct.common.ABSGGCache;
import com.zjtelcom.cpct.common.CacheConstants;
import com.zjtelcom.cpct.common.CacheManager;
import com.zjtelcom.cpct.common.IDacher;
import com.zjtelcom.cpct.dao.eagle.EagleTagMapper;
import com.zjtelcom.cpct.model.EagleTag;
import com.zjtelcom.cpct.model.EagleTagAdaption;
import com.zjtelcom.cpct.util.GroovyUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;


public class EagleTagCache extends ABSGGCache<EagleTag> {

    private static final Logger LOG = Logger.getLogger(EagleTagCache.class);

    public EagleTagCache(String name, long refreshTimeSeconds) {
        super(name, refreshTimeSeconds);
    }

    @Override
    public Object missCallBack(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        LOG.debug("key not in cache,query database: " + key);
        String name = key.substring(0, key.lastIndexOf("_"));
        String fitDomain = key.substring(key.lastIndexOf("_") + 1);
        EagleTag value = getBean(EagleTagMapper.class).selectByNameAndDomain(name, fitDomain);
        if (null != value) {
            this.addOne(key, value);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void synIt() {
        try {
            EagleTagMapper eagleTagMapper = getBean(EagleTagMapper.class);
            long startTime = System.currentTimeMillis();
            this.removeAll();
            List<EagleTag> tagList = eagleTagMapper.queryAll();
            IDacher<EagleTagAdaption> cache = CacheManager.getInstance().getCache(
                CacheConstants.TAG_ADAPTION_CACHE_NAME);
            for (EagleTag tag : tagList) {
                String key = tag.getSourceTableColumnName() + "_" + tag.getFitDomain();
                String adaptionCacheKey = key + "_CACHE";
                EagleTagAdaption adaption = cache.queryOne(adaptionCacheKey);
                if (null != adaption) {
                    boolean flag = (boolean) GroovyUtil.invokeMethod(adaption.getScript(),
                        "process", tag);
                    if (flag) {
                        this.addOne(key, tag);
                    }
                }
                else {
                    this.addOne(key, tag);
                }

            }
            LOG.info("EagleTagCache cache end, consume " + (System.currentTimeMillis() - startTime) + " ms.");
            
        }
        catch (Exception e) {
            LOG.error("init EagleTagCache error", e);
        }
    }

}
