package com.zjtelcom.cpct.dto.strategy;

import java.util.Date;

public class MktStrategyConf {
    /**
     * 策略配置Id
     */
    private Long mktStrategyConfId;

    /**
     * 策略配置名称
     */
    private String mktStrategyConfName;

    /**
     * 生效时间
     */
    private Date beginTime;

    /**
     * 失效时间
     */
    private Date endTime;

    /**
     * 策略下发渠道
     */
    private Long channelsId;

    public Long getMktStrategyConfId() {
        return mktStrategyConfId;
    }

    public void setMktStrategyConfId(Long mktStrategyConfId) {
        this.mktStrategyConfId = mktStrategyConfId;
    }

    public String getMktStrategyConfName() {
        return mktStrategyConfName;
    }

    public void setMktStrategyConfName(String mktStrategyConfName) {
        this.mktStrategyConfName = mktStrategyConfName;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getChannelsId() {
        return channelsId;
    }

    public void setChannelsId(Long channelsId) {
        this.channelsId = channelsId;
    }
}