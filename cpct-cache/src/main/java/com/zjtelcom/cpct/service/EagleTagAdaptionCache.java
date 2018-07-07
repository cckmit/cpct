package com.zjtelcom.cpct.service;


import com.zjtelcom.cpct.common.ABSGGCache;
import com.zjtelcom.cpct.dao.eagle.EagleTagAdaptionMapper;
import com.zjtelcom.cpct.model.EagleTagAdaption;
import org.apache.log4j.Logger;

import java.util.List;


public class EagleTagAdaptionCache extends ABSGGCache<EagleTagAdaption> {

    private static final Logger LOG = Logger.getLogger(EagleTagAdaptionCache.class);

    public EagleTagAdaptionCache(String name, long refreshTimeSeconds) {
        super(name, refreshTimeSeconds);
    }

    @Override
    public Object missCallBack(String key) {
        return null;
    }

    @Override
    public void synIt() {
        try {
            EagleTagAdaptionMapper adaptionMapper = getBean(EagleTagAdaptionMapper.class);
            long startTime = System.currentTimeMillis();
            this.removeAll();
            List<EagleTagAdaption> apaptionList = adaptionMapper.queryAll();
            for (EagleTagAdaption adaption : apaptionList) {
                this.addOne(adaption.getSourceTableColumnName() + "_" + adaption.getFitDomain()+"_"+adaption.getAdapClassify(),
                    adaption);
            }
            LOG.info("EagleTagAdaptionCache cache end, consume " + (System.currentTimeMillis() - startTime) + " ms.");
            
        }
        catch (Exception e) {
            LOG.error("init EagleTagAdaptionCache error", e);
        }
    }

}
