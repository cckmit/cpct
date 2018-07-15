package com.zjtelcom.cpct.service;


import com.zjtelcom.cpct.common.ABSGGCache;
import com.zjtelcom.cpct.dao.eagle.TriggerMapper;
import com.zjtelcom.cpct.model.Trigger;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import java.util.List;


public class TriggerCache extends ABSGGCache<Trigger> {

    private static final Logger LOG = Logger.getLogger(TriggerCache.class);

    public TriggerCache(String name, long refreshTimeSeconds) {
        super(name, refreshTimeSeconds);
    }

    @Override
    public Object missCallBack(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return getBean(TriggerMapper.class).selectByPrimaryKey(Integer.valueOf(key));
    }

    @Override
    public void synIt() {
        try {
            List<Trigger> triggerList = getBean(TriggerMapper.class).queryAll();
            long startTime = System.currentTimeMillis();
            this.removeAll();
            for (Trigger trigger : triggerList) {
                addOne(trigger.getConditionId().toString(), trigger);
            }
            LOG.info("TriggerMapper cache end, consume " + (System.currentTimeMillis() - startTime) + " ms.");

        }
        catch (Exception e) {
            LOG.error("init TriggerMapper error", e);
        }
    }

}
