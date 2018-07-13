package com.zjtelcom.cpct.common;

import com.zjtelcom.cpct.service.ApplicationContextRegister;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.statistics.StatisticsGateway;
import org.springframework.web.context.ContextLoader;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * <b>版权：</b>Copyright (c) 2015 .<br>
 * <b>工程：</b>TestEache<br>
 * <b>文件：</b>ABSGGCache.java<br>
 * <b>创建时间：</b>2015-5-26 下午3:35:08<br>
 * <p>
 * <b>基于全局的缓存.</b><br>
 * </p>
 *
 * @author XIX
 * @see [相关类/方法]
 * @since [产品/模块版本]
 * @param <T>
 */
public abstract class ABSGGCache<T> implements IDacher<T> {
    private DacheConfig dacheConfig = new DacheConfig();
    private String cacheName;
    private Date lastsyntime;
    private Date creatDate;
    private boolean isCacheAble = true;
    protected Object bean;

    public ABSGGCache(String name, long refreshTimeSeconds)
    {
        // 缓存名称，最大列表是2000个，不允许磁盘写入，允许永远持有，定期刷新机制
        init(name, 15000, false, true, 0, 0);
    }

    private void init(String name, int maxElementsInMemory, boolean overflowToDisk, boolean eternal,
            long timeToLiveSeconds, long timeToIdleSeconds) {
        getDacheConfig().eternal = eternal;
        getDacheConfig().name = name;
        getDacheConfig().maxElementsInMemory = maxElementsInMemory;
        getDacheConfig().overflowToDisk = overflowToDisk;
        getDacheConfig().timeToIdleSeconds = timeToIdleSeconds;
        getDacheConfig().timeToLiveSeconds = timeToLiveSeconds;

        creatDate = new Date();
        cacheName = name;
    }

    public Date queryCreateDate()
    {
        return creatDate;
    }

    public Date queryLastRefreshDate4GGCache()
    {
        return lastsyntime;
    }

    public int getSize()
    {
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        if (cache == null) {
            return -1;
        }

        return cache.getKeysWithExpiryCheck().size();
    }

    public StatisticsGateway getStatistics()
    {
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        if (cache == null) {
            return null;
        }

        return cache.getStatistics();
    }

    public void removeAll()
    {
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        if (cache == null) {
            return;
        }
        cache.removeAll();
    }

    public boolean isExpired(Element element)
    {
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        if (cache == null) {
            return false;
        }

        return cache.isExpired(element);
    }

    public boolean isDisabled()
    {
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        if (cache == null) {
            return false;
        }
        return cache.isDisabled();
    }

    public void disableUseViaEhcache()
    {
        isCacheAble = false;
    }

    public Date getCreatDate()
    {
        return creatDate;
    }

    /**
     *
     * 如果缓存中不存在数据，则向物理库进行数据同步
     *
     */
    public void synItWithLock()
    {
        // Cache cache = CacheManager.getInstance().getCache(cacheName);
        synchronized (this.getClass()) {
            // if (cache != null && cache.getKeysWithExpiryCheck().size() <= 0) {
            synIt();
            lastsyntime = new Date();
            // }
        }
    }

    @Override
    public String getCacheName() {
        return cacheName;
    }

    @Override
    public void addOne(String key, T value)
    {
        CacheManager.getInstance().getCache(cacheName).put(new Element(key, value));
    }

    public void deleteOne(String key)
    {
        CacheManager.getInstance().getCache(cacheName).remove(key);
    }

    public boolean isExpired(String key)
    {
        CacheManager.getInstance().getCache(cacheName).evictExpiredElements();
        return CacheManager.getInstance().getCache(cacheName).isKeyInCache(key);
    }

    @SuppressWarnings("unchecked")
    public T queryOne(String key)
    {
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        if(null == cache){
            return (T)missCallBack(key);
        }

        Element eKey = cache.get(key);

        if (eKey == null) {
            return (T)missCallBack(key);
        }

        return (T) eKey.getObjectValue();
    }
    
    public Cache getCache() {
        if (isCacheAble) {
            return CacheManager.getInstance().getCache(cacheName);
        }
        return null;
    }

    public DacheConfig getDacheConfig() {
        return dacheConfig;
    }

    public Date getLastsyntime() {
        return lastsyntime;
    }

    public Map<String, T> queryAll()
    {
        Map<String, T> rlt = new HashMap<String, T>(100);
        Cache cache = CacheManager.getInstance().getCache(cacheName);
        // if (cache != null && cache.getKeysWithExpiryCheck().size() <= 0) {
        // synItWithLock();
        // }

        for (Object key : cache.getKeysWithExpiryCheck()) {
            rlt.put((String) key, (T) cache.get(key).getObjectValue());
        }
        return rlt;
    }
    
    public <B> B getBean(Class<B> clazz){
        B springBean = null;
        if(null == this.bean){
//            springBean = ContextLoader.getCurrentWebApplicationContext().getBean(clazz);
            springBean = ApplicationContextRegister.getApplicationContext() .getBean(clazz);
            this.bean = springBean;
        }
        springBean = (B)this.bean;
        return springBean;
    }
}
