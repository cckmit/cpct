package com.zjtelcom.cpct.dubbo.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

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
    private ArrayList<Long> channelList;

    /**
     * 下发城市
     */
    private ArrayList<Integer> areaIdList;

    /**
     * 过滤规则id集合
     */
    private ArrayList<Long>  filterRuleIdList;

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


    public ArrayList<Long> getChannelList() {
        return channelList;
    }

    public void setChannelList(ArrayList<Long> channelList) {
        this.channelList = channelList;
    }

    public ArrayList<Integer> getAreaIdList() {
        return areaIdList;
    }

    public void setAreaIdList(ArrayList<Integer> areaIdList) {
        this.areaIdList = areaIdList;
    }

    public ArrayList<Long> getFilterRuleIdList() {
        return filterRuleIdList;
    }

    public void setFilterRuleIdList(ArrayList<Long> filterRuleIdList) {
        this.filterRuleIdList = filterRuleIdList;
    }
}