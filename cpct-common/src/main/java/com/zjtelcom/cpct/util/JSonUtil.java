package com.zjtelcom.cpct.util;


import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

/**
 * <p>
 * <b>版权：</b>Copyright (c) 2012 .<br>
 * <b>工程：</b><br>
 * <b>文件：</b>JSonUtil.java<br>
 * <b>创建时间：</b>2012-11-23 下午3:23:23<br>
 * <p>
 * <b>JSon工具类.</b><br>
 * 1 关于日期类型的转换，一律用yyyy-MM-dd HH:mm:ss.SSS 展示，而非缺省的long型不可观的显示<br>
 * </p>
 * 
 * @author GXT
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class JSonUtil {

    /**
     * @Fields DefaultDateFormat : Date类型的显示格式；另外一种方式是在JsonBean添加字段注解，注解方式如下：\n
     * @JsonFormat(timezone ="Asia/Shanghai",pattern =
     *                      "yyyy-MM-dd HH:mm:ss.SSS")
     */
    private static final String DEFAULTDATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * @Fields mapper :对象映射
     */
    private static ObjectMapper mapper;
    /**
     * @Fields fieldMapper :
     *         该json的key为ObjectBean的字段名,字段名完全匹配，不存在大小写问题;另外一种方式是在JsonBean添加类注解
     *         ，注解方式如下：@JsonAutoDetect(fieldVisibility =
     *         JsonAutoDetect.Visibility.ANY, getterVisibility =
     *         JsonAutoDetect.Visibility.NONE, setterVisibility =
     *         JsonAutoDetect.Visibility.NONE)
     */
    private static ObjectMapper fieldMapper;

    private static ObjectMapper logFieldMapper;
    protected static Log logger = LogFactory.getLog(JSonUtil.class);
    static {
        mapper = new ObjectMapper();
        mapper.setDateFormat(new SimpleDateFormat(DEFAULTDATEFORMAT));
        mapper.setSerializationInclusion(Include.NON_NULL); // 不拼接value为null的类字段属性，简化字串显示，也可以在实体类上加注解
        // @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)实现

        // 不拼接value为null的map型的key，简化字串显示
        // mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        fieldMapper = new ObjectMapper();
        fieldMapper.setDateFormat(new SimpleDateFormat(DEFAULTDATEFORMAT));
        fieldMapper.setSerializationInclusion(Include.NON_NULL);
        // 不拼接value为null的类字段属性，简化字串显示，也可以在实体类上加注解
        // @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)实现

        // 不拼接value为null的map型的key，简化字串显示
        // fieldMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES,false);

        fieldMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);

        logFieldMapper = new ObjectMapper();
        logFieldMapper.setDateFormat(new SimpleDateFormat(DEFAULTDATEFORMAT));
        logFieldMapper.setSerializationInclusion(Include.NON_NULL);
        // 不拼接value为null的类字段属性，简化字串显示，也可以在实体类上加注解
        // @JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)实现

        logFieldMapper.configure(MapperFeature.USE_ANNOTATIONS, false);
        logFieldMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                .setVisibility(PropertyAccessor.SETTER, JsonAutoDetect.Visibility.NONE);
    }

    /**
     * <b> 基于Field模式的Bean对象转换成JsonString </b> <br>
     * <br>
     * 1 基于Bean的Field模式进行反射转换的<br>
     * 2 注意： 所有key均是bean对象中的字段 <br>
     * 3 支持来自继承的字段转换，无视访问符类型
     */
    public static String switch2StrViaField(final Object obj) {
        String rlt = null;
        try {
            rlt = fieldMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("write to json string error:" + obj, e);
        }
        return rlt;
    }

    /**
     * <b> 基于Field模式的Bean对象转换成JsonString，并人性化的展示 </b> <br>
     * <br>
     * 1 人性化展示 JsonString，debug调试模式下使用；<br>
     * 2 基于Bean的Field模式进行反射转换的<br>
     * 3 注意： 所有key均是bean对象中的字段 <br>
     * 4 支持来自继承的字段转换，无视访问符类型
     */
    public static String switch2PrettyStrViaField(final Object obj){
        String rlt = null;
        try {
            rlt = fieldMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("write to json string error:" + obj, e);
        }
        return rlt;
    }

    /**
     * 可以将人性化的Json字串转换成便于日志单行展示的Json字串信息
     * 
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    public static String log2String(final String obj) {
        try {
            JsonNode cc = logFieldMapper.readValue(obj, JsonNode.class);
            if (cc == null) {
                return obj;
            } else {
                return cc.toString();
            }
        } catch (Exception e) {
            return obj;
        }
    }

    /**
     * <b> 基于Field模式的Json字符串转换成Bean对象 </b> <br>
     * <br>
     * 1 基于Bean的Field模式进行反射转换的<br>
     * 2 注意： 所有key均是bean对象中的字段 <br>
     * 3 支持来自继承的字段转换，无视访问符类型
     */
    public static <T> T switch2BeanViaField(final String jSonString, final Class<T> clazz){
        T rlt = null;
        if (jSonString == null) {
            return rlt;
        }
        try {
            rlt = fieldMapper.readValue(jSonString, clazz);
        } catch (IOException e) {
            logger.error("write to json string error:" + jSonString, e);
        }
        return rlt;

    }

    /**
     * <b> 基于Set模式的Json字符串转换成Bean对象 </b> <br>
     * <br>
     * 1 Jackson缺省方式进行转换；<br>
     * 2 基于Bean的Set模式进行反射转换的<br>
     * 3 注意： 所有key的名字来自set方法，且与Bean对象的字段名相比，会出现小写化的情况 <br>
     * 4 支持访问符为public的字段json转换，且优先级高于set模式
     */
    public static <T> T switch2Bean(final String jSonString, final Class<T> clazz){
        T rlt = null;
            if (jSonString == null) {
                return rlt;
            }
        try {
            rlt = mapper.readValue(jSonString, clazz);
        } catch (IOException e) {
            logger.error("write to json string error:" + jSonString, e);
        }
        return rlt;
    }

    /**
     * <b> 基于Set模式Bean对象转换成JsonString </b> <br>
     * <br>
     * 1 Jackson缺省方式进行转换；<br>
     * 2 基于Bean的Set模式进行反射转换的<br>
     * 3 注意： 所有key的名字来自set方法，且与Bean对象的字段名相比，会出现小写化的情况 <br>
     * 4 支持访问符为public的字段json转换，且优先级高于set模式
     */
    public static String switch2Str(final Object obj){
        String rlt = null;
        try {
            rlt = mapper.writeValueAsString(obj);
        } catch (IOException e) {
            logger.error("write to json string error:" + obj, e);
            return null;
        }

        return rlt;
    }

    /**
     * <b> 基于Set模式Bean对象转换成JsonString，并人性化的展示 </b> <br>
     * <br>
     * 1 Jackson缺省方式进行转换；<br>
     * 2 基于Bean的Set模式进行反射转换的<br>
     * 3 注意：所有key的名字来自set方法，且与Bean对象的字段名相比，会出现小写化的情况 <br>
     * 4 人性化展示Json字符串，常用于debug模式调试时使用 <br>
     * 5 支持访问符为public的字段json转换，且优先级高于set模式
     */
    public static String switch2PrettyStr(final Object obj){
        String rlt = null;
        try {
            rlt = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            logger.error("write to json string error:" + obj, e);
        }
        return rlt;
    }

    /**
     * 将json数据转化成map对象
     * @param json
     * @return map
     */
    public static  Map<String, Object> String2Map(String json){
        Map<String,Object> userData = null;
        // 读取JSON数据
        try {
            userData = mapper.readValue(json, Map.class);
        } catch (IOException e) {
            logger.error("write to json string error:" + json, e);
        }
        return userData;
    }
    
    /**
     * 校验json是否为空
     * @param obj json对象
     * @param key 属性的key
     * @param isArray 是否为数组
     * @return 是否为空
     */
//    public static boolean checkObjectNull(JSONObject obj, String key, boolean isArray) {
//        if (null == obj || null == obj.get(key) || "null".equals(obj.get(key))
//            || StringUtils.isEmpty(obj.getString(key))) {
//            return true;
//        }
//        else if (JSonUtil.isNull(obj.get(key))) {
//            return true;
//        }
//
//        if (isArray) {
//            if ("[]".equals(obj.get(key))) {
//                return true;
//            }
//        }
//
//        return false;
//    }
    
    /**
     * 转换成key,value这种格式的json array
     * @param params 需要转换的值
     * @return json array
     */
//    public static JSONArray convertToMapArray(Object params) {
//        JSONArray array = new JSONArray();
//        if (null == params) {
//            return array;
//        }
//        JSONObject json = JSONObject.fromObject(params);
//        Set<String> items = json.keySet();
//        for (String key : items) {
//            JSONObject obj = new JSONObject();
//            obj.put("key", key);
//            obj.put("value", json.get(key));
//            array.add(obj);
//        }
//        return array;
//    }
}
