package com.zjtelcom.cpct.service;


import com.zjtelcom.cpct.common.CacheConstants;
import com.zjtelcom.cpct.common.CacheManager;
import org.apache.log4j.Logger;


/**
 * User:liuxianxian 1061 Date:2017/6/27. Time:23:15. Todo: Copyright:南京星邺汇捷网络科技有限公司 Version:1.0
 */
public class CacheCenter {
    private static final Logger LOG = Logger.getLogger(CacheCenter.class);

    public void synCache() {
        LOG.debug("synchronize cache start.");

        if (CacheManager.getInstance().getCache(CacheConstants.TAG_ADAPTION_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.TAG_ADAPTION_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no TAG_ADAPTION_CACHE in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.EVENT_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.EVENT_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no event cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.CHANNEL_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.CHANNEL_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no channel cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no activity cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_POLICY_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_POLICY_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no activityPolicy cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.PPMPRODUCT_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.PPMPRODUCT_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no ppmproduct cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.SYSTEMPARAM_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.SYSTEMPARAM_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no systemparam cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.PUSH_TEMPLATE_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.PUSH_TEMPLATE_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no pushtemplate cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.POPUINFO_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.POPUINFO_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no popuinfo cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.MESSAGEINFO_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.MESSAGEINFO_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no messageinfo cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.VERBAL_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.VERBAL_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no verbal cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_CHANNEL_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_CHANNEL_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no acivitychannel cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.RECOMMEND_PRODUCT_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.RECOMMEND_PRODUCT_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no recommend product cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.OPERATION_TEMPLATE_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.OPERATION_TEMPLATE_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no operation template cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(
            CacheConstants.RECOMMEND_PRODUCT_CHANNEL_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(
                CacheConstants.RECOMMEND_PRODUCT_CHANNEL_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no recommend product cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.BLACKLIST_USER_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.BLACKLIST_USER_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no blacklist cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.CONDITION_MAPPING_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.CONDITION_MAPPING_NAME).synItWithLock();
        }
        else {
            LOG.debug("no condition cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_POLICY_RULE_RECOMMEND_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_POLICY_RULE_RECOMMEND_NAME).synItWithLock();
        }
        else {
            LOG.debug("no activity_policy_rule cache in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.TRIGGER_VALUE_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.TRIGGER_VALUE_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no TRIGGER_VALUE_CACHE in cache manager.");
        }

        if (CacheManager.getInstance().getCache(CacheConstants.TRIGGER_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.TRIGGER_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no TRIGGER_CACHE_NAME in cache manager.");
        }

        //大数据缓存
        if (CacheManager.getInstance().getCache(CacheConstants.TAG_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.TAG_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no TAG_CACHE_NAME in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.DATABASE_COPNFIG_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.DATABASE_COPNFIG_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no DATABASE_COPNFIG_CACHE_NAME in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.SOURCE_TABLE_DEF_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.SOURCE_TABLE_DEF_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no SOURCE_TABLE_DEF_CACHE_NAME in cache manager.");
        }
        if (CacheManager.getInstance().getCache(CacheConstants.SOURCE_TABLE_REF_CACHE_NAME) != null) {
            CacheManager.getInstance().getCache(CacheConstants.SOURCE_TABLE_REF_CACHE_NAME).synItWithLock();
        }
        else {
            LOG.debug("no SOURCE_TABLE_REF_CACHE_NAME in cache manager.");
        }

    }
}
