package com.zjtelcom.cpct.dto.strategy;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MktStrategyConf implements Serializable {
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
    private List<Long> channelList;

    /**
     * 下发城市
     */
    private List<Integer> areaIdList;

    /**
     * 过滤规则id集合
     */
    private List<Long>  filterRuleIdList;

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

    public List<Long> getChannelList() {
        return channelList;
    }

    public void setChannelList(List<Long> channelList) {
        this.channelList = channelList;
    }

    public List<Integer> getAreaIdList() {
        return areaIdList;
    }

    public void setAreaIdList(List<Integer> areaIdList) {
        this.areaIdList = areaIdList;
    }

    public List<Long> getFilterRuleIdList() {
        return filterRuleIdList;
    }

    public void setFilterRuleIdList(List<Long> filterRuleIdList) {
        this.filterRuleIdList = filterRuleIdList;
    }
}