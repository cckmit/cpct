package com.zjtelcom.cpct.common;

import com.zjtelcom.cpct.service.EagleTagAdaptionCache;
import com.zjtelcom.cpct.service.EagleTagCache;
import com.zjtelcom.cpct.service.TriggerCache;
import com.zjtelcom.cpct.service.TriggerValueCache;
import net.sf.ehcache.Cache;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * <p> <b>版权：</b>Copyright (c) 2015 .<br> <b>工程：</b>TBPO<br> <b>文件：</b>DacheManager.java<br>
 * <b>创建时间：</b>2015-6-9 上午9:16:35<br> <p> <b>缓存管理器.</b><br> </p>
 *
 * @author XIX
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@SuppressWarnings("rawtypes")
public class CacheManager {

    private volatile static CacheManager singleton;

    private Logger log = Logger.getLogger(CacheManager.class);

    private CacheManager() {
    }

    public static CacheManager getInstance() {
        if (singleton == null) {
            synchronized (CacheManager.class) {
                if (singleton == null) {
                    singleton = new CacheManager();
                    singleton.initCache();
                }
            }
        }
        return singleton;
    }

    private static Map<String, IDacher> dacaherMap = new ConcurrentHashMap<>();


    public void addCache(IDacher cache) {
        Cache cache2 = new Cache(cache.getDacheConfig().name,
            cache.getDacheConfig().maxElementsInMemory, cache.getDacheConfig().overflowToDisk,
            cache.getDacheConfig().eternal, cache.getDacheConfig().timeToLiveSeconds,
            cache.getDacheConfig().timeToIdleSeconds);

        net.sf.ehcache.CacheManager.getInstance().addCache(cache2);
        dacaherMap.put(cache.getCacheName(), cache);
    }

    public IDacher getCache(String cacheName) {
        return dacaherMap.get(cacheName);
    }

    private void initCache() {
        try {

            // 事件适配器缓存
            singleton.addCache(new EagleTagAdaptionCache(CacheConstants.TAG_ADAPTION_CACHE_NAME,
                86400));
            CacheManager.getInstance().getCache(CacheConstants.TAG_ADAPTION_CACHE_NAME).synItWithLock();
//
//            // 事件信息缓存
//            singleton.addCache(new EventCache(CacheConstants.EVENT_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.EVENT_CACHE_NAME).synItWithLock();
//
//            // 活动信息缓存
//            singleton.addCache(new ActivityInfoCache(CacheConstants.ACTIVITY_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_CACHE_NAME).synItWithLock();
//            // 活动策略信息缓存
//            singleton.addCache(new ActivityPolicyInfoCache(
//                CacheConstants.ACTIVITY_POLICY_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_POLICY_CACHE_NAME).synItWithLock();
//            // 展销品信息缓存
//            singleton.addCache(new PpmProductCache(CacheConstants.PPMPRODUCT_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.PPMPRODUCT_CACHE_NAME).synItWithLock();
//            // 渠道信息缓存
//            singleton.addCache(new ChannelCache(CacheConstants.CHANNEL_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.CHANNEL_CACHE_NAME).synItWithLock();
//
//            // 系统参数缓存
//            singleton.addCache(new SystemParamCache(CacheConstants.SYSTEMPARAM_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.SYSTEMPARAM_CACHE_NAME).synItWithLock();
//            // 销售品信息缓存
//            singleton.addCache(new RecommendProductCache(
//                CacheConstants.RECOMMEND_PRODUCT_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.RECOMMEND_PRODUCT_CACHE_NAME).synItWithLock();
//
//            // 推荐产品执行渠道缓存
//            singleton.addCache(new RecommendChannelCache(
//                CacheConstants.RECOMMEND_PRODUCT_CHANNEL_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(
//                CacheConstants.RECOMMEND_PRODUCT_CHANNEL_CACHE_NAME).synItWithLock();
//
//            // 推送信息缓存
//            singleton.addCache(new PushTemplateCache(CacheConstants.PUSH_TEMPLATE_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.PUSH_TEMPLATE_CACHE_NAME).synItWithLock();
//            // 弹框信息缓存
//            singleton.addCache(new PopuInfoCache(CacheConstants.POPUINFO_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.POPUINFO_CACHE_NAME).synItWithLock();
//            // 短信信息缓存
//            singleton.addCache(new MessageInfoCache(CacheConstants.MESSAGEINFO_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.MESSAGEINFO_CACHE_NAME).synItWithLock();
//            // 话术信息缓存
//            singleton.addCache(new VerbalCache(CacheConstants.VERBAL_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.VERBAL_CACHE_NAME).synItWithLock();
//
//            // 活动执行渠道缓存
//            singleton.addCache(new ActivityChannelCache(
//                CacheConstants.ACTIVITY_CHANNEL_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_CHANNEL_CACHE_NAME).synItWithLock();
//
//            // 模板信息缓存
//            singleton.addCache(new OperationTepmlateCache(
//                CacheConstants.OPERATION_TEMPLATE_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.OPERATION_TEMPLATE_CACHE_NAME).synItWithLock();
//            // 黑名单用户缓存
//            singleton.addCache(new BlackUserCache(CacheConstants.BLACKLIST_USER_CACHE_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.BLACKLIST_USER_CACHE_NAME).synItWithLock();
//
//            // 因子映射信息缓存
//            singleton.addCache(new ConditionMappingCache(CacheConstants.CONDITION_MAPPING_NAME,
//                5000));
//            CacheManager.getInstance().getCache(CacheConstants.CONDITION_MAPPING_NAME).synItWithLock();
//
//            //活动策略规则销售品id
//            singleton.addCache(new RecommendRulePolicyCache(
//                CacheConstants.ACTIVITY_POLICY_RULE_RECOMMEND_NAME, 5000));
//            CacheManager.getInstance().getCache(CacheConstants.ACTIVITY_POLICY_RULE_RECOMMEND_NAME).synItWithLock();
//
            // 大数据标签缓存
            singleton.addCache(new EagleTagCache(CacheConstants.TAG_CACHE_NAME, 86400));
            CacheManager.getInstance().getCache(CacheConstants.TAG_CACHE_NAME).synItWithLock();
//
//            // 大数据标签源表定义缓存
//            singleton.addCache(new EagleSourceTableDefCache(
//                CacheConstants.SOURCE_TABLE_DEF_CACHE_NAME, 86400));
//            CacheManager.getInstance().getCache(CacheConstants.SOURCE_TABLE_DEF_CACHE_NAME).synItWithLock();
//
//            // 大数据标签源表关系缓存
//            singleton.addCache(new EagleSourceTableRefCache(
//                CacheConstants.SOURCE_TABLE_REF_CACHE_NAME, 86400));
//            CacheManager.getInstance().getCache(CacheConstants.SOURCE_TABLE_REF_CACHE_NAME).synItWithLock();
//
//            // 大数据标签源表关系缓存
//            singleton.addCache(new EagleDatabaseConfCache(
//                CacheConstants.DATABASE_COPNFIG_CACHE_NAME, 86400));
//            CacheManager.getInstance().getCache(CacheConstants.DATABASE_COPNFIG_CACHE_NAME).synItWithLock();
//
            // 因子值缓存
            singleton.addCache(new TriggerValueCache(CacheConstants.TRIGGER_VALUE_CACHE_NAME, 5000));
            CacheManager.getInstance().getCache(CacheConstants.TRIGGER_VALUE_CACHE_NAME).synItWithLock();
//
            // 因子缓存
            singleton.addCache(new TriggerCache(CacheConstants.TRIGGER_CACHE_NAME, 5000));
            CacheManager.getInstance().getCache(CacheConstants.TRIGGER_CACHE_NAME).synItWithLock();

        } catch (Exception e) {
            log.error("初始化缓存失败.", e);
        }
    }

}
