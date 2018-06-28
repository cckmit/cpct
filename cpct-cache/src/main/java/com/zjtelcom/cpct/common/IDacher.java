package com.zjtelcom.cpct.common;

import net.sf.ehcache.statistics.StatisticsGateway;

import java.util.Date;
import java.util.Map;

/**
 * <p>
 * <b>版权：</b>Copyright (c) 2015 .<br>
 * <b>工程：</b>TBPO<br>
 * <b>文件：</b>IDacher.java<br>
 * <b>创建时间：</b>2015-6-9 上午9:08:50<br>
 * <p>
 * <b>缓存接口.</b><br>
 * </p>
 *
 * @author XIX
 * @see [相关类/方法]
 * @since [产品/模块版本]
 * @param <T>
 */
public interface IDacher<T> extends IGGCacheOPS{

    /**向缓存中添加一个属性*/
    void addOne(String key, T value);

    /**依据key查询缓存*/
    T queryOne(String key);
    
    void removeAll();

    /**依据key删除缓存*/
    void deleteOne(String key);

    /**当前缓存是否启用*/
    boolean isDisabled();

    /**当前缓存元素是否过期*/
    boolean isExpired(String key);

    /**获取缓存元素的数据量*/
    int getSize();

    /**获取缓存的事件监听*/
//    CacheEventListener getListener();

    /**获取缓存的配置*/
    DacheConfig getDacheConfig();

    /**获取缓存的名称*/
    String getCacheName();

    /**获取缓存的统计信息*/
    StatisticsGateway getStatistics();

    /**获取缓存的所有数据*/
    Map<String, T> queryAll();
    
    Date queryCreateDate();
    Date queryLastRefreshDate4GGCache();
    
    /**
     * 缓存未命中的回调
     * @param key 缓存key
     * @return 缓存值
     * @author 陶文武
     */
    Object missCallBack(String key);
}
