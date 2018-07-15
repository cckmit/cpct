package com.zjtelcom.cpct.service;


import com.zjtelcom.cpct.common.ABSGGCache;
import com.zjtelcom.cpct.dao.eagle.EagleSourceTableDefMapper;
import com.zjtelcom.cpct.model.EagleSourceTableDef;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;


public class EagleSourceTableDefCache extends ABSGGCache<EagleSourceTableDef> {

    private static final Logger LOG = Logger.getLogger(EagleSourceTableDefCache.class);

    public EagleSourceTableDefCache(String name, long refreshTimeSeconds) {
        super(name, refreshTimeSeconds);
    }

    @Override
    public Object missCallBack(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return getBean(EagleSourceTableDefMapper.class).selectByPrimaryKey(Integer.valueOf(key));
    }

    @Override
    public void synIt() {
        try {
            EagleSourceTableDefMapper sourceTableDefMapper = getBean(EagleSourceTableDefMapper.class);
            long startTime = System.currentTimeMillis();
            this.removeAll();

            List<EagleSourceTableDef> sourceTableDefList = sourceTableDefMapper.queryAll();
            for (EagleSourceTableDef sourceTableDef : sourceTableDefList) {
                this.addOne(sourceTableDef.getCtasTableDefinitionRowId().toString(),
                    sourceTableDef);
            }
            LOG.info("EagleSourceTableDefCache cache end, consume " + (System.currentTimeMillis() - startTime) + " ms.");
        }
        catch (Exception e) {
            LOG.error("init EagleSourceTableDefCache error", e);
        }
    }

}
