package com.zjtelcom.cpct.count.base.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.zjtelcom.cpct.domain.event.EventSorceDO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @Auther: anson
 * @Date: 2018/10/29
 * @Description:javabean转各种类型
 */
public class FormatUtil {

    /**
     * 方法说明：将bean转化为另一种bean实体
     * 
     * @param object
     * @param entityClass
     * @return
     */
    public static <T> T convertBean(Object object, Class<T> entityClass) {
        if(null == object) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(object,SerializerFeature.WriteMapNullValue), entityClass);
    }


    /**
     * 方法说明：对象转换
     * 
     * @param source	原对象
     * @param target	目标对象
     * @param ignoreProperties	排除要copy的属性
     * @return
     */
    public static <T> T copy(Object source, Class<T> target, String...ignoreProperties){
        T targetInstance = null;
        try {
            targetInstance = target.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(ArrayUtils.isEmpty(ignoreProperties)) {
            BeanUtils.copyProperties(source, targetInstance);
        }else {
            BeanUtils.copyProperties(source, targetInstance, ignoreProperties);
        }
        return targetInstance;

    }

    /**
     * 方法说明：对象转换(List)
     * 
     * @param list	原对象
     * @param target	目标对象
     * @param ignoreProperties	排除要copy的属性
     * @return
     */
    public static <T, E> List<T> copyList(List<E> list, Class<T> target, String...ignoreProperties){
        List<T> targetList = new ArrayList<>();
        if(CollectionUtils.isEmpty(list)) {
            return targetList;
        }
        for(E e : list) {
            targetList.add(copy(e, target, ignoreProperties));
        }
        return targetList;
    }

    /**
     * 方法说明：map转化为对象
     * 
     * @param map
     * @param t
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static <T> T mapToObject(Map<String, Object> map, Class<T> t) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        T instance = t.newInstance();
        org.apache.commons.beanutils.BeanUtils.populate(instance, map);
        return instance;
    }

    /**
     * 方法说明：对象转化为Map
     * 
     * @param object
     * @return
     */
    public static Map<String, Object> objectToMap(Object object){
        return convertBean(object, Map.class);
    }


    public static void main(String[] args) {
        EventSorceDO eventSorceDO=new EventSorceDO();
        eventSorceDO.setEvtSrcCode("123");
        eventSorceDO.setEvtSrcId(11111L);
        Map<String, Object> map = FormatUtil.objectToMap(eventSorceDO);
        map.put("id",null);
        map.put("href","/eventSorce/123456780000");
        String str1 = JSONObject.toJSONString(map, SerializerFeature.WriteMapNullValue);
        System.out.println(str1);


//        Map < String , Object > jsonMap = new HashMap< String , Object>();
//        jsonMap.put("a",1);
//        jsonMap.put("b","b");
//        jsonMap.put("c",null);
//        jsonMap.put("d","s");
//        String str = JSONObject.toJSONString(jsonMap,SerializerFeature.WriteMapNullValue);
//        System.out.println(str);


    }

}


