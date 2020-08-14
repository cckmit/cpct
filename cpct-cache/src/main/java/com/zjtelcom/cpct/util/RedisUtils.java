package com.zjtelcom.cpct.util;

import com.alibaba.fastjson.JSON;
import com.ctg.itrdc.cache.pool.CtgJedisPool;
import com.ctg.itrdc.cache.pool.CtgJedisPoolConfig;
import com.ctg.itrdc.cache.pool.CtgJedisPoolException;
import com.ctg.itrdc.cache.pool.ProxyJedis;
import com.github.benmanes.caffeine.cache.Cache;
import com.zjtelcom.cpct.dao.system.SysParamsMapper;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description
 * @Author pengy
 * @Date 2018/7/4 10:41
 */
@Service
public class RedisUtils {

    @Autowired(required = false)
    private CtgJedisPool ctgJedisPool;

    @Autowired
    private SysParamsMapper sysParamsMapper;

    @Autowired(required = false)
    Cache<String, Object> caffeineCache;


    /*public void setRedisExpiry() {
        ProxyJedis jedis = new ProxyJedis();
        try {
            jedis = ctgJedisPool.getResource();
            jedis.setpxnx()
        }catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public Object hget(final String key, String filed) {
        Object result = null;
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                String resultString = jedis.hget(key, filed);
                result = unserizlize(resultString);
                jedis.close();
            } catch (Throwable je) {
                je.printStackTrace();
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     *
     * 通过key获取所有客户信息
     * @param key
     * @return List<Map<String, Object>>
     */
    public Object hgetAllRedisList(final String key) {
        Object result = null;
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                Map<String, String> resultMap = jedis.hgetAll(key);
                List<Map<String, Object>> mapList = new ArrayList<>();
                for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    mapList.addAll((List<Map<String, Object>>) unserizlize(entry.getValue()));
                }
                result = mapList;
                jedis.close();
            } catch (Throwable je) {
                je.printStackTrace();
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     *
     * 通过key获取所有客户信息
     * @param key
     * @return List<String>
     */
    public Object hgetAll(final String key) {
        Object result = null;
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                Map<String, String> resultMap = jedis.hgetAll(key);
                List<String> mapList = new ArrayList<>();
                for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    mapList.addAll( (List<String>)unserizlize(entry.getValue()));
                }
                result = mapList;
                jedis.close();
            } catch (Throwable je) {
                je.printStackTrace();
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<String> hgetAllField(final String key) {
        List<String> fieldList = new ArrayList<>();
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                Map<String, String> resultMap = jedis.hgetAll(key);
                for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                    fieldList.add(entry.getKey());
                }
                jedis.close();
            } catch (Throwable je) {
                je.printStackTrace();
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fieldList;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setCache(final String key, Object value) {
        boolean result = false;
        try {
            // 存入本地缓存
            if (value != null) {
                caffeineCache.put(key, value);
            }
            // 存入redis缓存
            result = setRedis(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     * @return
     */
    public boolean set(final String key, Object value) {
        boolean result = false;
        try {
            // 存入redis缓存
            result = setRedis(key, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 读取缓存
     *
     * @param key
     * @return
     */
    public Object get(final String key) {
        Object result = null;
        caffeineCache.getIfPresent(key); // 缓存中存在相应数据，则返回；不存在返回null
        result = caffeineCache.asMap().get(String.valueOf(key));
        System.out.println("从本地获取缓存数据--->>>" + JSON.toJSONString(result));
        if (result != null) {
            return result;
        }
        // 从redis缓存取数据
        result = getRedis(key);
        // 本地缓存中无值则再存一次本地缓存
        if (result != null) {
            caffeineCache.put(key, result);
        }
        return result;
    }

    public boolean del(final  String key){
        boolean result = false;
        // 删除本地缓存
        caffeineCache.asMap().remove(key);
        // 删除redis缓存
        result = delRedis(key);
        return result;
    }


    /**
     * 更换集团redis方法
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setRedisUnit(final String key, Object value,int seconds) {
        boolean result = false;

        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                jedis.set(key, serialize(value));
                jedis.expire(key,seconds);
                result = true;
            } catch (Throwable je) {
                System.out.println("REDIS*********" + key);
                je.printStackTrace();
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            System.out.println("REDIS2*********" + key);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更换集团redis方法
     *
     * @param key
     * @param value
     * @return
     */
    public boolean setRedis(final String key, Object value) {
        boolean result = false;

        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                jedis.set(key, serialize(value));
                result = true;
            } catch (Throwable je) {
                System.out.println("REDIS*********" + key);
                je.printStackTrace();
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            System.out.println("REDIS2*********" + key);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 写入缓存设置时效时间
     *
     * @param key
     * @param value
     * @return
     */
//    public boolean set(final String key, Object value, Long expireTime, TimeUnit timeUnit) {
//        boolean result = false;
//        try {
//            ValueOperations<Serializable, Object> operations = redisTemplate.opsForValue();
//            operations.set(key, value);
//            redisTemplate.expire(key, expireTime, timeUnit);
//            result = true;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return result;
//    }


    /**
     * 更换集团redis方法
     *
     * @param key
     * @return
     */
    private boolean delRedis(final String key) {
        boolean result = false;
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                if(jedis.exists(key)) {
                    jedis.del(key);
                    result = true;
                }
            } catch (Throwable je) {
                System.out.println("REDIS_EDL*********" + key);
                je.printStackTrace();
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            System.out.println("REDIS_EDL2*********" + key);
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 批量删除对应的value
     *
     * @param keys
     */
    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    /**
     * 批量删除key
     *
     * @param pattern
     */
//    public void removePattern(final String pattern) {
//        Set<Serializable> keys = redisTemplate.keys(pattern);
//        if (keys.size() > 0) {
//            redisTemplate.delete(keys);
//        }
//    }

    /**
     * 删除对应的value
     *
     * @param key
     */
//    public void removeKey(final String key) {
//        if (exists(key)) {
//            redisTemplate.delete(key);
//        }
//    }

    /**
     * 判断缓存中是否有对应的value
     *
     * @param key
     * @return
     */
    public boolean exists(final String key) {
        return existsRedis(key);
    }


    /**
     * 更换集团redis方法
     *
     * @param key
     * @return
     */
    public boolean existsRedis(final String key) {
        boolean result = false;
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                result = jedis.exists(key);
            } catch (Throwable je) {
                je.printStackTrace();
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 更换集团redis方法
     *
     * @param key
     * @return
     */
    public void remove(final String key) {
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                if (jedis.exists(key)){
                    jedis.del(key);
                }
            } catch (Throwable je) {
                je.printStackTrace();
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 更换集团redis方法
     *
     * @param key
     * @return
     */
    public Object getRedis(final String key) {
        Object result = null;
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                if(jedis.exists(key)) {
                    result = unserizlize(jedis.get(key));
                }
            } catch (Throwable je) {
                System.out.println("REDIS_GET*********" + key);
                je.printStackTrace();
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            System.out.println("REDIS_GET2*********" + key);
            e.printStackTrace();
        }
        return result;
    }



    /**
     * hash存储Redis
     * @param key
     * @param field
     * @param value
     * @return
     */
    public boolean hset(final String key, String field, Object value) {
        boolean result = false;
        try {
            ProxyJedis jedis = new ProxyJedis();
            try {
                jedis = ctgJedisPool.getResource();
                jedis.hset(key, field, serialize(value));
                result = true;
            } catch (Exception e) {
                System.out.println("REDIShset*********" + key);
                e.printStackTrace();
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            System.out.println("REDIShset2*********" + key);
            e.printStackTrace();
        }
        return result;
    }



    /**
     * 哈希 添加
     *
     * @param key
     * @param hashKey
     * @param value
     */
//    public void hmSet(String key, Object hashKey, Object value) {
//        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
//        hash.put(key, hashKey, value);
//    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @param hashKey
     * @return
     */
//    public Object hmGet(String key, Object hashKey) {
//        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
//        return hash.get(key, hashKey);
//    }

    /**
     * 哈希获取数据
     *
     * @param key
     * @return Map<HK, HV>
     */
//    public Object hKeys(String key) {
//        HashOperations<String, Object, Object> hash = redisTemplate.opsForHash();
//        return hash.entries(key);
//
//    }

    /**
     * 列表添加
     *
     * @param k
     * @param v
     */
//    public void lPush(String k, Object v) {
//        ListOperations<String, Object> list = redisTemplate.opsForList();
//        list.rightPush(k, v);
//    }

    /**
     * 列表获取
     *
     * @param k
     * @param l
     * @param l1
     * @return
     */
//    public List<Object> lRange(String k, long l, long l1) {
//        ListOperations<String, Object> list = redisTemplate.opsForList();
//        return list.range(k, l, l1);
//    }

    /**
     * 集合添加
     *
     * @param key
     * @param value
     */
//    public void add(String key, Object value) {
//        SetOperations<String, Object> set = redisTemplate.opsForSet();
//        set.add(key, value);
//    }

    /**
     * 集合获取
     *
     * @param key
     * @return
     */
//    public Set<Object> setMembers(String key) {
//        SetOperations<String, Object> set = redisTemplate.opsForSet();
//        return set.members(key);
//    }

    /**
     * 有序集合添加
     *
     * @param key
     * @param value
     * @param scoure
     */
//    public void zAdd(String key, Object value, double scoure) {
//        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
//        zset.add(key, value, scoure);
//    }

    /**
     * 有序集合获取
     *
     * @param key
     * @param scoure
     * @param scoure1
     * @return
     */
//    public Set<Object> rangeByScore(String key, double scoure, double scoure1) {
//        ZSetOperations<String, Object> zset = redisTemplate.opsForZSet();
//        return zset.rangeByScore(key, scoure, scoure1);
//    }
//
//    /**
//     * 查询该key下所有值
//     *
//     * @param key 查询的key
//     * @return Map<HK, HV>
//     */
//    public Object hget(String key) {
//        return hashOperations.entries(key);
//    }


    /**
     * 通过表达式匹配获取所有key
     */
//    public Set<Object> keys(String pattern) {
//        Set<Object> set = redisTemplate.keys(pattern);
//        return set;
//    }


//    private CtgJedisPool initCatch() {
//
//
//        List<HostAndPort> hostAndPortList = new ArrayList();
//        // 接入机的ip和端口号
////        HostAndPort host = new HostAndPort("134.108.0.57", 41701);
//        HostAndPort host = new HostAndPort("134.96.231.228", 40201);
//        hostAndPortList.add(host);
//
//        GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
//        poolConfig.setMaxIdle(5); //最大空闲连接数
//        poolConfig.setMaxTotal(10); // 最大连接数（空闲+使用中），不超过应用线程数，建议为应用线程数的一半
//        poolConfig.setMinIdle(5); //保持的最小空闲连接数
//        poolConfig.setMaxWaitMillis(3000);
//
//        CtgJedisPoolConfig config = new CtgJedisPoolConfig(hostAndPortList);
//
////        config.setDatabase(4970).setPassword("bss_cpcp_pocpro_user#bssCpc_ro").setPoolConfig(poolConfig).setPeriod(1000).setMonitorTimeout(100);
//        config.setDatabase(4970).setPassword("bss_cpct_common_user#bss_cpct_common_user123").setPoolConfig(poolConfig).setPeriod(1000).setMonitorTimeout(100);
//
//        CtgJedisPool pool = new CtgJedisPool(config);
//
//        return pool;
//    }


    public static void main(String[] args) throws CtgJedisPoolException {


        List<HostAndPort> hostAndPortList = new ArrayList();
        // 接入机的ip和端口号
        HostAndPort host = new HostAndPort("134.96.231.228", 40201);
        hostAndPortList.add(host);

        GenericObjectPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(5); //最大空闲连接数
        poolConfig.setMaxTotal(10); // 最大连接数（空闲+使用中），不超过应用线程数，建议为应用线程数的一半
        poolConfig.setMinIdle(5); //保持的最小空闲连接数
        poolConfig.setMaxWaitMillis(3000);

        CtgJedisPoolConfig config = new CtgJedisPoolConfig(hostAndPortList);

        config.setDatabase(4970).setPassword("bss_cpct_common_user#bss_cpct_common_user123").setPoolConfig(poolConfig).setPeriod(1000).setMonitorTimeout(100);

        CtgJedisPool pool = new CtgJedisPool(config);

        ProxyJedis jedis = new ProxyJedis();
        try {
            jedis = pool.getResource();
            //sendCommand 可能会抛出 运行时异常
            //jedis.set("test", "123");
            //sendCommand 可能会抛出 运行时异常
            //jedis.get("test");
            System.out.println(unserizlize(jedis.get("mktCampaignResp_test")));
            jedis.close();
        } catch (Throwable je) {
            je.printStackTrace();
            jedis.close();
        }
        pool.close();

    }

    /**
     * 对象序列化为字符串
     *
     * @param obj
     * @return
     */

    public static String serialize(Object obj) {
        String serStr = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            serStr = byteArrayOutputStream.toString("ISO-8859-1");
            serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
            objectOutputStream.close();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serStr;
    }

    /**
     * 字符串反序列化为对象
     *
     * @param serStr
     * @return
     */
    public static Object unserizlize(String serStr) {
        Object newObj = null;
        try {
            if(serStr != null) {
                String redStr = java.net.URLDecoder.decode(serStr, "UTF-8");
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(redStr.getBytes("ISO-8859-1"));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                newObj = objectInputStream.readObject();
                objectInputStream.close();
                byteArrayInputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newObj;
    }


    public String getRedisOrSysParams(String key) {
        String value = "";
        Object status = get(key);
        if (status != null) {
            value = status.toString();
        }else {
            List<Map<String, String>> sysFilList = sysParamsMapper.listParamsByKey(key);
            if (sysFilList != null && !sysFilList.isEmpty()) {
                value = sysFilList.get(0).get("value");
                set(key, value);
            }
        }
        return value;
    }

    public List<String> getListRedisOrSysParams(String key) {
        String value = getRedisOrSysParams(key);
        String[] split = value.split(",");
        List<String> list = Arrays.asList(split);
        return list;
    }

    // 获取开关：0为关，1为开。true为开，false为关。
    public boolean getSwitch(String key) {
        String redisOrSysParams = getRedisOrSysParams(key);
        if ("1".equals(redisOrSysParams)) {
            return true;
        }
        return false;
    }

}