package com.zjtelcom.cpct.service;

import com.zjtelcom.cpct.common.ABSGGCache;
import com.zjtelcom.cpct.model.EagleDatabaseConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class EagleDatabaseConfCache extends ABSGGCache<EagleDatabaseConfig> {

    private static final Logger LOG = Logger.getLogger(EagleDatabaseConfCache.class);

    public static final String CACHE_DB2_KEY = "DB2";

    public EagleDatabaseConfCache(String name, long refreshTimeSeconds) {
        super(name, refreshTimeSeconds);
    }

    @Override
    public Object missCallBack(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return null;
//        return getBean(EagleDatabaseConfigMapper.class).selectByPrimaryKey(Integer.valueOf(key));
    }

    @Override
    public void synIt() {
        try {
//            EagleDatabaseConfigMapper databaseConfigMapper = getBean(EagleDatabaseConfigMapper.class);
            long startTime = System.currentTimeMillis();
            this.removeAll();

            // 因为这里表里只有一条记录所以直接用固定的KEY
//            List<EagleDatabaseConfig> databaseConfList = databaseConfigMapper.queryAll();
//            for (EagleDatabaseConfig databaseConfig : databaseConfList) {
//                this.addOne(CACHE_DB2_KEY, databaseConfig);
//            }
            LOG.info("EagleDatabaseConfCache cache end, consume " + (System.currentTimeMillis() - startTime) + " ms.");
        }
        catch (Exception e) {
            LOG.error("init EagleDatabaseConfCache error", e);
        }
    }

}
