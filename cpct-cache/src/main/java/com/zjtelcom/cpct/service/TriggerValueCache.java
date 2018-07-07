package com.zjtelcom.cpct.service;


import com.zjtelcom.cpct.common.ABSGGCache;
import com.zjtelcom.cpct.dao.eagle.TriggerValueMapper;
import com.zjtelcom.cpct.model.TriggerValue;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.List;


public class TriggerValueCache extends ABSGGCache<TriggerValue> {

    private static final Logger LOG = Logger.getLogger(TriggerValueCache.class);

    public TriggerValueCache(String name, long refreshTimeSeconds) {
        super(name, refreshTimeSeconds);
    }

    @Override
    public Object missCallBack(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        String[] values = key.split("@");
        return getBean(TriggerValueMapper.class).queryByLeftAndValue(values[2], values[1],
            values[0]);
    }

    @Override
    public void synIt() {
        try {
            List<TriggerValue> triggerValues = getBean(TriggerValueMapper.class).queryAll();
            long startTime = System.currentTimeMillis();
            this.removeAll();
            for (TriggerValue value : triggerValues) {
                addOne(
                    value.getValueId().toString() + "@" + value.getDomain() + "@"
                        + value.getShowValue(), value);
            }
            LOG.info("TriggerValue cache end, consume " + (System.currentTimeMillis() - startTime) + " ms.");
            

        }
        catch (Exception e) {
            LOG.error("init TriggerValueMapper error", e);
        }
    }

}
